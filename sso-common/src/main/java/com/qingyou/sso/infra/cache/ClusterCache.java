package com.qingyou.sso.infra.cache;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class ClusterCache implements Cache {
    private AsyncMap<String, Object> cache;

    public ClusterCache(Vertx vertx) {
        vertx.sharedData().<String,Object>getClusterWideMap("clusterCache")
                .onSuccess(map -> cache = map)
                .onFailure(cause -> log.error("ClusterCache initialize error", cause));
    }


    @Override
    public Future<Boolean> exists(String key) {
        return cache.get(key).map(Objects::nonNull);
    }

    @Override
    public Future<Integer> exists(Collection<String> keys) {
        AtomicInteger counter = new AtomicInteger();
        List<Future<Boolean>> futures = new ArrayList<>();
        for (String key : keys) {
            var f = this.exists(key).onSuccess(v -> {
                if (v) counter.incrementAndGet();
            });
            futures.add(f);
        }
        return Future.all(futures).map(v -> counter.get());
    }

    @Override
    public Future<Boolean> delete(String key) {
        return cache.remove(key).map(Objects::nonNull);
    }

    @Override
    public Future<Boolean> deleteBatch(Collection<String> keys) {
        AtomicInteger counter = new AtomicInteger();
        List<Future<Boolean>> futures = new ArrayList<>();
        for (String key : keys) {
            var f = this.delete(key).onSuccess(v -> {
                if (v) counter.incrementAndGet();
            });
            futures.add(f);
        }
        return Future.all(futures).map(v -> counter.get() > 0);
    }

    @Override
    public <T> Future<Void> set(String key, T obj) {
        return cache.put(key, obj);
    }

    @Override
    public <T> Future<Void> set(String key, T obj, Duration expire) {
        return cache.put(key, obj, expire.toMillis());
    }

    @Override
    public <T> Future<Void> setBatch(Collection<Map.Entry<String, T>> maps) {
        List<Future<Void>> futures = new ArrayList<>();
        maps.forEach(map -> futures.add(cache.put(map.getKey(), map.getValue())));
        return Future.all(futures).mapEmpty();
    }

    @Override
    public <T> Future<Void> setBatch(Collection<Map.Entry<String, T>> maps, Duration expire) {
        List<Future<Void>> futures = new ArrayList<>();
        maps.forEach(map -> futures.add(cache.put(map.getKey(), map.getValue(), expire.toMillis())));
        return Future.all(futures).mapEmpty();
    }

    @Override
    public <T> Future<T> get(String key, Class<T> clazz) {
        return cache.get(key).flatMap(obj -> {
            if (obj == null) return Future.failedFuture("Not Found");
            return Future.succeededFuture(clazz.cast(obj));
        });
    }

    @Override
    public <T> Future<List<T>> getBatch(Collection<String> keys, Class<T> clazz) {
        List<Future<T>> futures = new ArrayList<>();
        for (String key : keys) {
            var f = this.get(key, clazz);
            futures.add(f);
        }
        return Future.all(futures).map(v -> futures.stream().map(Future::result).toList());
    }
}
