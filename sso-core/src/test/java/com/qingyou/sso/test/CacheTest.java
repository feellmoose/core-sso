package com.qingyou.sso.test;

import com.qingyou.sso.infra.cache.RedisCache;
import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CacheTest {

    record Record(int id, String name, String value) {
    }

    //    @Test
    void set() {
        var redis = Redis.createClient(Vertx.vertx(), "redis://localhost:6379");
        var cache = new RedisCache(redis, Duration.of(60, ChronoUnit.SECONDS));
        var list = new ArrayList<Record>();
        for (int i = 0; i < 1000; i++) {
            list.add(new Record(i, "name" + i, "value" + i));
        }
        var maps = list.stream().collect(Collectors.toMap(Record::name, record -> record));
        cache.setBatch(maps.entrySet())
                .flatMap(x -> cache.getBatch(List.of("name1", "name2"), Record.class))
                .map(y -> {
                    y.forEach(System.out::println);
                    return y;
                }).toCompletionStage().toCompletableFuture().join();
    }


}
