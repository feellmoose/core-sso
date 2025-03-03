package com.qingyou.sso.api;

import io.vertx.ext.web.RoutingContext;

public interface Error {
    void error(RoutingContext routingContext);
}
