package com.qingyou.sso.infra.response;


import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

import java.util.function.Function;

/**
 * A service handle routing params and convert to response results
 * @param <T>
 */
public interface ServiceApi<T> extends Function<RoutingContext, Future<T>> {
}
