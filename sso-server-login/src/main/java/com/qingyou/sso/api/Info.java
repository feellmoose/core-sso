package com.qingyou.sso.api;

import com.qingyou.sso.api.result.LoginResult;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface Info {
    Future<LoginResult> info(RoutingContext routingContext);
}
