package com.qingyou.sso.auth.internal;

import com.qingyou.sso.auth.api.dto.Info;
import com.qingyou.sso.auth.exception.AuthException;
import io.vertx.core.Future;


public interface Enforcement<T extends Info> {
    Future<Boolean> enforce(T info);

    default Future<Void> enforceAndThrows(T info) throws AuthException {
        return enforce(info).flatMap(res -> {
            if (!res) return Future.failedFuture(new AuthException("unknown reason"));
            return Future.succeededFuture();
        });
    }
}
