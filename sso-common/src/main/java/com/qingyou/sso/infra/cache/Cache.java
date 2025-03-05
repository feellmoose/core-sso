package com.qingyou.sso.infra.cache;

import io.vertx.core.Future;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Cache {
    Future<Boolean> exists(String key);

    Future<Boolean> delete(String key);

    <T> Future<Void> set(String key, T obj);

    <T> Future<Void> set(String key, T obj, Duration expire);

    <T> Future<T> get(String key, Class<T> clazz);

}
