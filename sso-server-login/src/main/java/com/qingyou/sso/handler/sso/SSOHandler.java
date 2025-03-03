package com.qingyou.sso.handler.sso;

import com.qingyou.sso.api.Info;
import com.qingyou.sso.api.Login;
import com.qingyou.sso.api.Logout;
import com.qingyou.sso.api.State;
import com.qingyou.sso.api.result.LoginResult;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.service.BaseSSOService;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import lombok.AllArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@AllArgsConstructor
public class SSOHandler implements Login, Logout, State, Info {

    private final BaseSSOService baseSSOService;

    @Override
    public Future<LoginResult> info(RoutingContext routingContext) {
        Session session = routingContext.session();
        if (session == null) throw new BizException(ErrorType.Showed.Params, "session not exist, user already logged out");
        Long userId = session.get("userId");
        return baseSSOService.userinfo(userId);
    }

    @Override
    public Future<LoginResult> login(RoutingContext routingContext) {
        var authorization = routingContext.request().getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic"))
            throw new BizException(ErrorType.Showed.Params, "Authorization format incorrect");
        byte[] base64 = Base64.getDecoder().decode(authorization.substring(6));
        String[] words = new String(base64, StandardCharsets.UTF_8).split(":");
        if (words.length != 2) throw new BizException(ErrorType.Showed.Params, "Authorization format incorrect");
        if (words[0].isBlank()) throw new BizException(ErrorType.Showed.Params, "Username format incorrect");
        if (words[1].isBlank()) throw new BizException(ErrorType.Showed.Params, "Password format incorrect");
        return baseSSOService.login(words[0], words[1]).onSuccess(loginResult ->
            routingContext.session().put("userId", loginResult.userId())
        );
    }

    @Override
    public Future<LoginResult> logout(RoutingContext routingContext) {
        Session session = routingContext.session();
        if (session == null) throw new BizException(ErrorType.Showed.Params, "session not exist, user already logged out");
        Long userId = session.get("userId");
        return baseSSOService.logout(userId).onSuccess(loginResult ->
            routingContext.session().destroy()
        );
    }

    @Override
    public Future<Boolean> state(RoutingContext routingContext) {
        Session session = routingContext.session();
        if (session == null) throw new BizException(ErrorType.Showed.Params, "session not exist, user already logged out");
        Long userId = session.get("userId");
        return baseSSOService.state(userId);
    }

}
