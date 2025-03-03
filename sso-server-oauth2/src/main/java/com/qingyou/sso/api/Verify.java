package com.qingyou.sso.api;

import io.vertx.ext.web.RoutingContext;

public interface Verify {
    void verify(RoutingContext routingContext);
}
