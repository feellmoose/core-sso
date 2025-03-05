package com.qingyou.sso.infra.cache;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public class DefaultCache implements Cache {
    private final AsyncMap<String, Object> cache;

    private DefaultCache(AsyncMap<String,Object> cache) {
        this.cache = cache;
    }

    public static Future<Cache> build(Vertx vertx) {
        if (vertx.isClustered()) {
            return vertx.sharedData().<String,Object>getClusterWideMap("clusterCache")
                    .map(cache -> (Cache) new DefaultCache(cache))
                    .onFailure(cause -> log.error("ClusterCache initialize error", cause));
        } else {
            return vertx.sharedData().<String,Object>getLocalAsyncMap("clusterCache")
                    .map(cache -> (Cache) new DefaultCache(cache))
                    .onFailure(cause -> log.error("ClusterCache initialize error", cause));
        }
    }

    @Override
    public Future<Boolean> exists(String key) {
        return cache.get(key).map(Objects::nonNull);
    }

    @Override
    public Future<Boolean> delete(String key) {
        return cache.remove(key).map(Objects::nonNull);
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
    public <T> Future<@Nullable T> get(String key, Class<T> clazz) {
        return cache.get(key).map(v -> v == null ? null : clazz.cast(v));
    }

}
