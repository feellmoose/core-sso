package com.qingyou.sso.handler.platform;

import com.qingyou.sso.api.result.LoginResult;
import com.qingyou.sso.service.BaseSSOService;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public class Oauth2SSOHandlerRegistry implements SSOHandlerRegistry {

    @Override
    public CustomSSOHandler getSSOHandler(BaseSSOService ssoService) {
        return new CustomSSOHandler() {

            private final BaseSSOService baseSSOService = ssoService;

            @Override
            public String getName(){
                return "oauth2";
            }
            
            @Override
            public Future<LoginResult> login(RoutingContext routingContext) {
                return null;
            }

            @Override
            public Future<LoginResult> register(RoutingContext routingContext) {
                return null;
            }
        };
    }
}
