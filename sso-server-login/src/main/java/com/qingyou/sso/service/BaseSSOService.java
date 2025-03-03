package com.qingyou.sso.service;

import com.qingyou.sso.api.result.LoginResult;
import io.vertx.core.Future;

public interface BaseSSOService {
    Future<LoginResult> login(String username, String password);
    Future<LoginResult> logout(Long userId);
    Future<LoginResult> userinfo(Long userId);
    Future<Boolean> state(Long userId);
}
