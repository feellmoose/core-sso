package com.qingyou.sso.api;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface SendEmail {
    Future<Boolean> email(RoutingContext routingContext);
}
