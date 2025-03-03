package com.qingyou.sso.api;

import io.vertx.ext.web.RoutingContext;

public interface Refresh {
    void refresh(RoutingContext routingContext);
}
