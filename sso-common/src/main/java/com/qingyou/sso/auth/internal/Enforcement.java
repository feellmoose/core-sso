package com.qingyou.sso.auth.internal;

import com.qingyou.sso.auth.api.dto.Action;
import com.qingyou.sso.auth.exception.AuthException;
import io.vertx.core.Future;


public interface Enforcement {
    Future<Boolean> enforce(Action info);

    default Future<Void> enforceAndThrows(Action info) throws AuthException {
        return enforce(info).flatMap(res -> {
            if (!res) return Future.failedFuture(new AuthException("unknown reason"));
            return Future.succeededFuture();
        });
    }
}
