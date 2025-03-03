package com.qingyou.sso.router.sso;

import com.qingyou.sso.handler.platform.CustomHandler;
import com.qingyou.sso.infra.response.GlobalHttpResponse;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRouterHandler implements Handler<Router> {
    private final CustomHandler customHandler;
    @Getter
    private final String name;

    public CustomRouterHandler(CustomHandler customHandler, String name) {
        this.customHandler = customHandler;
        this.name = name;
    }

    @Override
    public void handle(Router router) {
        router.post("/sso/login/" + name).handler(GlobalHttpResponse.wrap(customHandler::login, log));
        router.post("/sso/register/" + name).handler(GlobalHttpResponse.wrap(customHandler::register, log));
    }
}
