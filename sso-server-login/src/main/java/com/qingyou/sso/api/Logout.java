package com.qingyou.sso.api;

import com.qingyou.sso.api.result.LoginResult;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface Logout {
    Future<LoginResult> logout(RoutingContext routingContext);
}
