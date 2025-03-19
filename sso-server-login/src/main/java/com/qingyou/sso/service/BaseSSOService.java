package com.qingyou.sso.service;

import com.qingyou.sso.api.dto.ThirdPartySSOUserInfo;
import com.qingyou.sso.api.result.LoginResult;
import io.vertx.core.Future;

public interface BaseSSOService {
    Future<LoginResult> login(String username, String password);
    Future<LoginResult> login(ThirdPartySSOUserInfo thirdPartyUserInfo, DataChecker dataChecker);
    Future<LoginResult> register(String username, String password);
    Future<LoginResult> registerThirdParty(String username, String password, ThirdPartySSOUserInfo thirdPartyUserInfo, DataChecker dataChecker);
    Future<LoginResult> changePassword(String username, String changed, ThirdPartySSOUserInfo thirdPartyUserInfo, DataChecker dataChecker);
    Future<LoginResult> logout(Long userId);
    Future<LoginResult> userinfo(Long userId);
    Future<Boolean> state(Long userId);
}
