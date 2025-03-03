package com.qingyou.sso.infra.response;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;

@AllArgsConstructor
public class GlobalHttpResponseWrap<T> implements Handler<RoutingContext> {

    private final ServiceApi<T> serviceApi;
    private final Logger log;

    @Override
    public void handle(RoutingContext routingContext) {
        try {
            Future<T> future;
            try {
                future = serviceApi.apply(routingContext);
            } catch (Throwable ex) {
                GlobalHttpResponse.fail(routingContext, ex,log);
                return;
            }
            GlobalHttpResponse.end(routingContext, future,log);
        } catch (Throwable ex) {
            log.error("Un handle RuntimeException(message={})", ex.getMessage(), ex);
            routingContext.fail(500);
        }
    }


}