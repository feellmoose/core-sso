package com.qingyou.sso.service;

import com.qingyou.sso.api.result.LoginResult;
import com.qingyou.sso.domain.user.User;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.infra.repository.domain.AccountRepository;
import com.qingyou.sso.infra.repository.domain.UserRepository;
import com.qingyou.sso.utils.PasswordEncodeUtils;
import io.vertx.core.Future;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DefaultBaseSSOService implements BaseSSOService {
    private UserRepository userRepository;
    private AccountRepository accountRepository;

    @Override
    public Future<LoginResult> login(String username, String password) {
        return accountRepository.findByUsername(username).flatMap(account -> {
            if (account == null) throw new BizException(ErrorType.Inner.Login, "Account not found");
            var words = PasswordEncodeUtils.encode(password, account.getSalt());
            if (!words.encoded().equals(account.getPassword()))
                throw new BizException(ErrorType.Inner.Login, "Password incorrect");
            return userRepository.findById(account.getUserId());
        }).map(user -> {
            if (user == null) throw new BizException(ErrorType.Inner.Login, "User not found");
            return new LoginResult(user.getId(), user.getName());
        });
    }

    @Override
    public Future<LoginResult> logout(Long userId) {
        return userinfo(userId);
    }

    @Override
    public Future<LoginResult> userinfo(Long userId) {
        if (userId == null) throw new BizException(ErrorType.Inner.Login, "User already logged out");
        return userRepository.findById(userId).map(user -> {
            if (user == null) throw new BizException(ErrorType.Inner.Login, "User not found");
            return new LoginResult(user.getId(), user.getName());
        });
    }

    @Override
    public Future<Boolean> state(Long userId) {
        return Future.succeededFuture(userId != null);
    }
}