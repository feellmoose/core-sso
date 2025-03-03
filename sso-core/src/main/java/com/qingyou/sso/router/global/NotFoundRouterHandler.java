package com.qingyou.sso.router.global;

import com.qingyou.sso.api.dto.Result;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;

import static com.qingyou.sso.infra.response.GlobalHttpResponse.APPLICATION_JSON;
import static com.qingyou.sso.infra.response.GlobalHttpResponse.CONTENT_TYPE_HEADER;

public class NotFoundRouterHandler implements Handler<Router> {
    @Override
    public void handle(Router router) {
        router.route().handler(routingContext -> routingContext.response().setStatusCode(404)
                .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                .end(Json.encodeToBuffer(Result.failed(404, "Not Found"))));
    }
}
