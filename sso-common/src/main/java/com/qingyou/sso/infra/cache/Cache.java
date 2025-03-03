package com.qingyou.sso.infra.cache;

import io.vertx.core.Future;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Cache {
    Future<Boolean> exists(String key);

    Future<Integer> exists(Collection<String> keys);

    Future<Boolean> delete(String key);

    Future<Boolean> deleteBatch(Collection<String> keys);

    <T> Future<Void> set(String key, T obj);

    <T> Future<Void> set(String key, T obj, Duration expire);

    <T> Future<Void> setBatch(Collection<Map.Entry<String, T>> maps);

    <T> Future<Void> setBatch(Collection<Map.Entry<String, T>> maps, Duration expire);

    <T> Future<T> get(String key, Class<T> clazz);

    <T> Future<List<T>> getBatch(Collection<String> keys, Class<T> clazz);
}
