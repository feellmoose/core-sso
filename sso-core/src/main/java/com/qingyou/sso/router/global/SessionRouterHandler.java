package com.qingyou.sso.router.global;

import com.qingyou.sso.infra.config.ConfigurationSource;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.SessionStore;
import lombok.AllArgsConstructor;

public class SessionRouterHandler implements Handler<Router> {
    private final SessionStore sessionStore;
    private final ConfigurationSource configurationSource;

    public SessionRouterHandler(SessionStore sessionStore, ConfigurationSource configurationSource) {
        this.sessionStore = sessionStore;
        this.configurationSource = configurationSource;
    }

    @Override
    public void handle(Router router) {
        var config = configurationSource.getConfiguration().security().cookie();
        router.route().handler(
                SessionHandler.create(sessionStore)
                        .setCookieHttpOnlyFlag(config.httpOnly())
                        .setCookieSecureFlag(config.secure())
                        .setCookieMaxAge(config.maxAge())
                        .setSessionTimeout(config.timeout())
                        .setSessionCookiePath(config.path())
                        .setSessionCookieName(config.name())
        );
    }
}
