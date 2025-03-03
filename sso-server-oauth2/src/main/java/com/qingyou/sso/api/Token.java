package com.qingyou.sso.api;

import io.vertx.ext.web.RoutingContext;

public interface Token {
    void token(RoutingContext routingContext);
}
