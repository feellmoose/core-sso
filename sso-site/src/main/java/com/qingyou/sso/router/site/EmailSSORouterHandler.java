package com.qingyou.sso.router.site;


import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailSSORouterHandler implements Handler<Router> {

    @Override
    public void handle(Router router) {
        router.route("/sso/*").handler(StaticHandler.create());
    }

}
