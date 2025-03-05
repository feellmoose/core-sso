package com.qingyou.sso.service;

import com.qingyou.sso.api.param.Code;
import com.qingyou.sso.api.param.Email;
import com.qingyou.sso.api.param.UsernamePassword;
import com.qingyou.sso.api.result.LoginResult;
import io.vertx.core.Future;

public interface EmailSSOService {
    Future<Boolean> email(Email email);
    Future<LoginResult> login(Code code);
    Future<LoginResult> register(Code code);
    Future<LoginResult> setAccount(UsernamePassword usernamePassword, Long userId);
}
