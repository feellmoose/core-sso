package com.qingyou.sso.handler.platform;

import com.qingyou.sso.api.result.LoginResult;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public class GoogleRegisterHandler implements CustomRegisterHandler {
    @Override
    public Future<LoginResult> login(RoutingContext routingContext) {
        return null;
    }

    @Override
    public Future<LoginResult> register(RoutingContext routingContext) {
        return null;
    }
}
