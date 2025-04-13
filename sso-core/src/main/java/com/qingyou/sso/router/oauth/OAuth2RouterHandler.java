package com.qingyou.sso.router.oauth;

import com.qingyou.sso.handler.ErrorPageHandler;
import com.qingyou.sso.handler.OAuth2Handler;
import com.qingyou.sso.handler.OAuth2ParamHandler;
import com.qingyou.sso.serviece.OAuth2Service;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OAuth2RouterHandler implements Handler<Router> {
    private final OAuth2Handler oauth2Handler;
    private final ErrorPageHandler errorHandler = new ErrorPageHandler();

    public OAuth2RouterHandler(OAuth2Service oauth2Service) {
        this.oauth2Handler = new OAuth2Handler(oauth2Service);
    }

    @Override
    public void handle(Router router) {
        router.get("/oauth/authorize").handler(OAuth2ParamHandler.authorization()::inject).handler(oauth2Handler::authorize);

        router.post("/oauth/token").handler(BodyHandler.create()).handler(OAuth2ParamHandler.token()::inject).handler(oauth2Handler::token);

        router.get("/oauth/userinfo").handler(OAuth2ParamHandler.info()::inject).handler(oauth2Handler::info);

        router.get("/oauth/.well-known/openid-configuration");
        //{
        //  "issuer": "string",
        //  "authorization_endpoint": "string",
        //  "token_endpoint": "string",
        //  "userinfo_endpoint": "string",
        //  "jwks_uri": "string",
        //  "response_types_supported": [
        //    "string"
        //  ],
        //  "scopes_supported": [
        //    "string"
        //  ],
        //  "id_token_signing_alg_values_supported": [
        //    "string"
        //  ]
        //}

        router.get("/oauth/logout");
        //

        router.get("/oauth/jwks");

        router.post("/oauth/password").handler(BodyHandler.create()).handler(OAuth2ParamHandler.password()::inject).handler(oauth2Handler::password);

        router.route("/error").handler(errorHandler::error);
    }
}
