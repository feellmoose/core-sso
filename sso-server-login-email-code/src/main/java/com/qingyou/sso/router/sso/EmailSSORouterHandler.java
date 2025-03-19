package com.qingyou.sso.router.sso;

import com.qingyou.sso.handler.platform.EmailSSOHandler;
import com.qingyou.sso.infra.response.GlobalHttpResponse;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailSSORouterHandler implements Handler<Router> {

    private final EmailSSOHandler emailSSOHandler;

    public EmailSSORouterHandler(EmailSSOHandler emailSSOHandler) {
        this.emailSSOHandler = emailSSOHandler;
    }

    @Override
    public void handle(Router router) {
        router.route("/sso/email/*").handler(BodyHandler.create());
        router.get("/sso/email/code").handler(GlobalHttpResponse.wrap(emailSSOHandler::email, log));
        router.get("/sso/email/login").handler(GlobalHttpResponse.wrap(emailSSOHandler::login, log));
        router.get("/sso/email/register").handler(GlobalHttpResponse.wrap(emailSSOHandler::register, log));
    }
}
