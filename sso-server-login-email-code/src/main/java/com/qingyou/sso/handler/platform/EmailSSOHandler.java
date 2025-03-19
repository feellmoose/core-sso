package com.qingyou.sso.handler.platform;

import com.qingyou.sso.api.Login;
import com.qingyou.sso.api.Register;
import com.qingyou.sso.api.SendEmail;
import com.qingyou.sso.api.param.Code;
import com.qingyou.sso.api.param.Email;
import com.qingyou.sso.api.result.LoginResult;
import com.qingyou.sso.service.EmailSSOService;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public class EmailSSOHandler implements Login, Register, SendEmail{

    private final EmailSSOService emailSSOService;

    public EmailSSOHandler(EmailSSOService emailSSOService) {
        this.emailSSOService = emailSSOService;
    }

    @Override
    public Future<Boolean> email(RoutingContext routingContext) {
        Email email = routingContext.body().asPojo(Email.class);
        return emailSSOService.email(email);
    }

    @Override
    public Future<LoginResult> login(RoutingContext routingContext) {
        Code code = routingContext.body().asPojo(Code.class);
        return emailSSOService.login(code).onSuccess(u ->
                routingContext.session().put("userId",u.userId()));
    }

    @Override
    public Future<LoginResult> register(RoutingContext routingContext) {
        Code code = routingContext.body().asPojo(Code.class);
        return emailSSOService.register(code).onSuccess(u ->
                routingContext.session().put("userId",u.userId()));
    }

}
