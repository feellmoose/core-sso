package com.qingyou.sso.router.sso;

import com.qingyou.sso.handler.platform.CustomSSOHandler;
import com.qingyou.sso.infra.response.GlobalHttpResponse;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomSSORouterHandler implements Handler<Router> {
    private final CustomSSOHandler customHandler;

    public CustomSSORouterHandler(CustomSSOHandler customHandler) {
        this.customHandler = customHandler;
    }

    @Override
    public void handle(Router router) {
        BodyHandler bodyHandler = BodyHandler.create();
        router.post("/sso/login/" + customHandler.getName())
                .handler(bodyHandler)
                .handler(GlobalHttpResponse.wrap(customHandler::login, log));
        router.post("/sso/register/" + customHandler.getName())
                .handler(bodyHandler)
                .handler(GlobalHttpResponse.wrap(customHandler::register, log));
    }
}
