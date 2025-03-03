package com.qingyou.sso.handler;

import com.qingyou.sso.api.*;
import com.qingyou.sso.api.param.OAuth2Params;
import com.qingyou.sso.infra.response.OAuth2HttpResponse;
import com.qingyou.sso.serviece.OAuth2Service;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class OAuth2Handler implements Authorization, Token, Verify, Refresh, Info, Password {
    private final OAuth2Service oauthService;

    @Override
    public void authorize(RoutingContext routingContext) {
        try {
            var validate = routingContext.<OAuth2Params.ValidationResult<OAuth2Params.Authorization>>get("params");
            if (!validate.success()) {
                OAuth2HttpResponse.failRedirect(routingContext, "/error", validate.error(), log);
                return;
            }
            var params = validate.result();
            var userId = routingContext.session().<Long>get("userId");

            switch (params.responseType()) {
                case code -> oauthService.authorization(userId, params)
                            .onSuccess(code -> {
                                String url = params.redirectURI() + "?code=" + code.code();
                                if (params.state() != null && !params.state().isBlank()) url += "&state=" + params.state();
                                OAuth2HttpResponse.redirect(routingContext,url);
                            }).onFailure(ex ->OAuth2HttpResponse.failRedirect(routingContext,"/error", ex, log));

                case token -> oauthService.implicitAuthorization(userId, params)
                            .onSuccess(accessToken -> {
                                String url = params.redirectURI() + "#access_token=" + accessToken.access_token()
                                        + "&expires_in=" + accessToken.expires_in()
                                        + "&token_type=" + accessToken.token_type()
                                        + "&scope=" + accessToken.scope();
                                if (params.state() != null && !params.state().isBlank()) url += "&state=" + params.state();
                                OAuth2HttpResponse.redirect(routingContext,url);
                            }).onFailure(ex -> OAuth2HttpResponse.failRedirect(routingContext,"/error", ex, log));
            }
        } catch (Throwable e) {
            OAuth2HttpResponse.failRedirect(routingContext, "/error", e, log);
        }
    }

    @Override
    public void token(RoutingContext routingContext) {
        var validate = routingContext.<OAuth2Params.ValidationResult<OAuth2Params.Token>>get("params");
        if (!validate.success()) {
            OAuth2HttpResponse.fail(routingContext, validate.error(), log);
            return;
        }
        var params = validate.result();

        try {
            switch (params.grantType()) {
                case refresh_token -> {
                    var v = params.refresh();
                    if (!v.success()) {
                        OAuth2HttpResponse.fail(routingContext, validate.error(), log);
                        return;
                    }
                    oauthService.refresh(v.result())
                            .onSuccess(accessToken -> OAuth2HttpResponse.success(routingContext, accessToken))
                            .onFailure(ex -> OAuth2HttpResponse.fail(routingContext, ex, log));
                }
                case authorization_code -> {
                    var v = params.verify();
                    if (!v.success()) {
                        OAuth2HttpResponse.fail(routingContext, validate.error(), log);
                        return;
                    }
                    oauthService.verify(v.result())
                            .onSuccess(accessToken -> OAuth2HttpResponse.success(routingContext, accessToken))
                            .onFailure(ex -> OAuth2HttpResponse.fail(routingContext, ex, log));
                }
                case client_credentials -> {
                    OAuth2HttpResponse.success(routingContext, "not supported");//TODO
                }
            }

        } catch (Throwable e) {
            OAuth2HttpResponse.fail(routingContext, e, log);
        }
    }

    @Override
    public void verify(RoutingContext routingContext) {
        var validate = routingContext.<OAuth2Params.ValidationResult<OAuth2Params.Verify>>get("params");
        if (!validate.success()) {
            OAuth2HttpResponse.fail(routingContext, validate.error(), log);
            return;
        }
        var params = validate.result();

        try {
            oauthService.verify(params)
                    .onSuccess(accessToken -> OAuth2HttpResponse.success(routingContext, accessToken))
                    .onFailure(ex -> OAuth2HttpResponse.fail(routingContext, ex, log));
        } catch (Throwable e) {
            OAuth2HttpResponse.fail(routingContext, e, log);
        }
    }

    @Override
    public void refresh(RoutingContext routingContext) {
        var validate = routingContext.<OAuth2Params.ValidationResult<OAuth2Params.Refresh>>get("params");
        if (!validate.success()) {
            OAuth2HttpResponse.fail(routingContext, validate.error(), log);
            return;
        }
        var params = validate.result();

        try {
            oauthService.refresh(params)
                    .onSuccess(userInfo -> OAuth2HttpResponse.success(routingContext, userInfo))
                    .onFailure(ex -> OAuth2HttpResponse.fail(routingContext, ex, log));
        } catch (Throwable e) {
            OAuth2HttpResponse.fail(routingContext, e, log);
        }
    }

    @Override
    public void info(RoutingContext routingContext) {
        var validate = routingContext.<OAuth2Params.ValidationResult<OAuth2Params.Info>>get("params");
        if (!validate.success()) {
            OAuth2HttpResponse.fail(routingContext, validate.error(), log);
            return;
        }
        var params = validate.result();
        try {
            oauthService.info(params)
                    .onSuccess(userInfo -> OAuth2HttpResponse.success(routingContext, userInfo))
                    .onFailure(ex -> OAuth2HttpResponse.fail(routingContext, ex, log));
        } catch (Throwable e) {
            OAuth2HttpResponse.fail(routingContext, e, log);
        }
    }

    @Override
    public void password(RoutingContext routingContext) {
        var validate = routingContext.<OAuth2Params.ValidationResult<OAuth2Params.Password>>get("params");
        if (!validate.success()) {
            OAuth2HttpResponse.fail(routingContext, validate.error(), log);
            return;
        }
        var params = validate.result();
        try {
            oauthService.password(params)
                    .onSuccess(userInfo -> OAuth2HttpResponse.success(routingContext, userInfo))
                    .onFailure(ex -> OAuth2HttpResponse.fail(routingContext, ex, log));
        } catch (Throwable e) {
            OAuth2HttpResponse.fail(routingContext, e, log);
        }
    }
}
