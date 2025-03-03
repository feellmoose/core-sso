package com.qingyou.sso.api;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface State {
    Future<Boolean> state(RoutingContext routingContext);
}
