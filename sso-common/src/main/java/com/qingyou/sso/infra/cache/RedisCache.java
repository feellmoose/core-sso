package com.qingyou.sso.infra.cache;

import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.redis.client.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@AllArgsConstructor
public class RedisCache implements Cache {
    private static final Logger log = LoggerFactory.getLogger(RedisCache.class);
    private final Redis redis;
    private final Duration expire;

    @Override
    public Future<Boolean> exists(String key) {
        return redis.send(Request.cmd(Command.EXISTS, key)).map(response -> response.toInteger() == 1);
    }

    @Override
    public Future<Integer> exists(Collection<String> keys) {
        var request = Request.cmd(Command.EXISTS);
        keys.forEach(request::arg);
        return redis.send(request).map(Response::toInteger);
    }

    @Override
    public Future<Boolean> delete(String key) {
        return redis.send(Request.cmd(Command.DEL, key)).map(response -> response.toInteger() == 1);
    }

    @Override
    public Future<Boolean> deleteBatch(Collection<String> keys) {
        var request = Request.cmd(Command.DEL);
        keys.forEach(request::arg);
        return redis.send(request).map(response -> response.toInteger() == 1);
    }

    @Override
    public <T> Future<Void> set(String key, T obj) {
        return set(key, Json.encode(obj), expire);
    }

    @Override
    public <T> Future<Void> set(String key, T obj, Duration expire) {
        return redis.batch(
                List.of(Request.cmd(Command.SET, key, Json.encode(obj)), Request.cmd(Command.PEXPIRE, key, expire.toMillis()))
        ).map(responses -> null);
    }

    @Override
    public <T> Future<Void> setBatch(Collection<Map.Entry<String, T>> maps) {
        return setBatch(maps, expire);
    }

    @Override
    public <T> Future<Void> setBatch(Collection<Map.Entry<String, T>> maps, Duration expire) {
        return redis.send(Request.cmd(Command.MULTI)).onSuccess(response -> {
            if (response.type() != ResponseType.MULTI) throw new BizException(ErrorType.Inner.Default,"Cache response error cmd:Multi");
        }).<Void>flatMap(response -> {
            var expireMillis = expire.toMillis();
            var requests = maps.stream().flatMap(map ->
                    Stream.of(Request.cmd(Command.SET, map.getKey(), Json.encode(map.getValue())), Request.cmd(Command.PEXPIRE, map.getKey(), expireMillis))
            ).toList();
            return redis.batch(requests).map(responses -> null);
        }).onSuccess(v -> {
            redis.send(Request.cmd(Command.EXEC));
        }).onFailure(ex -> {
            redis.send(Request.cmd(Command.DISCARD));
            log.error("Redis Cache batch exec failed", ex);
        });
    }

    @Override
    public <T> Future<T> get(String key, Class<T> clazz) {
        return redis.send(Request.cmd(Command.GET, key)).map(response -> {
            if (response == null) return null;
            return Json.decodeValue(response.toBuffer(), clazz);
        });
    }

    @Override
    public <T> Future<List<T>> getBatch(Collection<String> keys, Class<T> clazz) {
        return redis.send(Request.cmd(Command.MULTI)).onSuccess(response -> {
            if (response.type() != ResponseType.MULTI) throw new BizException(ErrorType.Inner.Default,"Cache response error cmd:Multi");
        }).flatMap(v -> {
            var requests = keys.stream().map(key -> Request.cmd(Command.GET, key)).toList();
            return redis.batch(requests);
        }).flatMap(v -> {
            return redis.send(Request.cmd(Command.EXEC)).map(responses ->
                    responses.stream().map(response -> Json.decodeValue(response.toBuffer(), clazz)).toList()
            );
        }).onFailure(ex -> {
            redis.send(Request.cmd(Command.DISCARD));
            log.error("Redis Cache batch exec failed", ex);
        });
    }

}
