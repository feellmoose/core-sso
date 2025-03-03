package com.qingyou.sso.router.sso;

import com.qingyou.sso.handler.sso.SSOHandler;
import com.qingyou.sso.infra.response.GlobalHttpResponse;
import com.qingyou.sso.service.BaseSSOService;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginRouterHandler  implements Handler<Router> {
    private final SSOHandler ssoHandler;

    public LoginRouterHandler(BaseSSOService baseSSOService) {
        this.ssoHandler =  new SSOHandler(baseSSOService);
    }

    @Override
    public void handle(Router router) {
        router.get("/sso/login").handler(GlobalHttpResponse.wrap(ssoHandler::login, log));
        router.get("/sso/logout").handler(GlobalHttpResponse.wrap(ssoHandler::logout, log));
        router.get("/sso/state").handler(GlobalHttpResponse.wrap(ssoHandler::state, log));
        router.get("/sso/info").handler(GlobalHttpResponse.wrap(ssoHandler::info, log));
    }

}
