package com.qingyou.sso.utils;

import io.smallrye.mutiny.Uni;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class UniConvertUtils {

    public static <T> Future<T> toFuture(Uni<T> uni) {
        var promise = Promise.<T>promise();
        uni.subscribe().with(promise::complete, promise::fail);
        return promise.future();
    }

    public static <T> Uni<T> toUni(Future<T> future) {
        return Uni.createFrom().completionStage(future.toCompletionStage());
    }

}
