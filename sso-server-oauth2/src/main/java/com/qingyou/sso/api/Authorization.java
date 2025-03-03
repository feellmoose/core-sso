package com.qingyou.sso.api;

import io.vertx.ext.web.RoutingContext;

public interface Authorization {
    void authorize(RoutingContext routingContext);
}
