package com.qingyou.sso.service;

import com.qingyou.sso.api.dto.ThirdPartySSOUserInfo;
import com.qingyou.sso.api.result.LoginResult;
import com.qingyou.sso.domain.user.Account;
import com.qingyou.sso.domain.user.User;
import com.qingyou.sso.domain.user.UserInfo;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.infra.repository.domain.AccountRepository;
import com.qingyou.sso.infra.repository.domain.UserInfoRepository;
import com.qingyou.sso.infra.repository.domain.UserRepository;
import com.qingyou.sso.utils.PasswordEncodeUtils;
import com.qingyou.sso.utils.StringUtils;
import io.vertx.core.Future;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class DefaultBaseSSOService implements BaseSSOService {
    private UserRepository userRepository;
    private AccountRepository accountRepository;
    private UserInfoRepository userInfoRepository;

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
    public Future<LoginResult> login(ThirdPartySSOUserInfo userInfo, DataChecker dataChecker) {
        if (userInfo != null) {
            if (userInfo.email() != null) {
                return userRepository.findByEmail(userInfo.email()).flatMap(user -> {
                    if (user == null) throw new BizException(ErrorType.Inner.Login, "User not found");
                    return userInfoRepository.findByUserIdAndPlatformTypes(user.getId(), List.of(userInfo.info().platformType())).map(userInfos -> {
                        if (userInfos.isEmpty()) throw new BizException(ErrorType.Inner.Login, "Login method not register");
                        if (dataChecker.check(userInfos.get(0).getValue())) {
                            return new LoginResult(user.getId(), user.getName());
                        }
                        throw new BizException(ErrorType.Inner.Login, "Login failed, data check failed");
                    });
                });
            }
            if (userInfo.phone() != null) {
                return userRepository.findByPhone(userInfo.phone()).flatMap(user -> {
                    if (user == null) throw new BizException(ErrorType.Inner.Login, "User not found");
                    return userInfoRepository.findByUserIdAndPlatformTypes(user.getId(),List.of(userInfo.info().platformType())).map(userInfos -> {
                        if (userInfos.isEmpty()) throw new BizException(ErrorType.Inner.Login, "Login method not register");
                        if (dataChecker.check(userInfos.get(0).getValue())) {
                            return new LoginResult(user.getId(), user.getName());
                        }
                        throw new BizException(ErrorType.Inner.Login, "Login failed, data check failed");
                    });
                });
            }
            if (userInfo.name() != null) {
                return userRepository.findByName(userInfo.name()).flatMap(user -> {
                    if (user == null) throw new BizException(ErrorType.Inner.Login, "User not found");
                    return userInfoRepository.findByUserIdAndPlatformTypes(user.getId(), List.of(userInfo.info().platformType())).map(userInfos -> {
                        if (userInfos.isEmpty()) throw new BizException(ErrorType.Inner.Login, "Login method not register");
                        if (dataChecker.check(userInfos.get(0).getValue())) {
                            return new LoginResult(user.getId(), user.getName());
                        }
                        throw new BizException(ErrorType.Inner.Login, "Login failed, data check failed");
                    });
                });
            }
        }
        return Future.failedFuture(new BizException(ErrorType.Inner.Login, "User not found"));
    }

    @Override
    public Future<LoginResult> register(String username, String password) {
        return accountRepository.findByUsername(username).flatMap(account -> {
            if (account != null) throw new BizException(ErrorType.Showed.Auth,"Account of this username already exists");
            User register = new User();
            register.setName("Visitor_" + StringUtils.randomNum(8));
            return userRepository.insert(register);
        }).flatMap(user -> {
            var encoded = PasswordEncodeUtils.encode(password);
            Account account = new Account(
                    user.getId(),
                    username,
                    encoded.encoded(),encoded.salt(),
                    user);
            return accountRepository.insert(account).map(user);
        }).map(user ->  new LoginResult(user.getId(),user.getName()));
    }

    @Override
    public Future<LoginResult> registerByThirdParty(String username, ThirdPartySSOUserInfo userInfo) {
        User register = new User();
        register.setName("Visitor_" + StringUtils.randomNum(8));
        return userRepository.insert(register).flatMap(user -> {
            var encoded = PasswordEncodeUtils.encode("");
            Account account = new Account(
                    user.getId(),
                    username,
                    encoded.encoded(),encoded.salt(),
                    user);
            return accountRepository.insert(account)
                    .flatMap(v -> {
                        UserInfo registerInfo = new UserInfo();
                        registerInfo.setUser(user);
                        var info = userInfo.info();
                        registerInfo.setMetadata(info.metadata());
                        registerInfo.setPlatformType(info.platformType());
                        registerInfo.setDataType(info.dataType());
                        return userInfoRepository.insert(registerInfo);
                    }).map(user);
        }).map(user ->  new LoginResult(user.getId(),user.getName()));
    }

    @Override
    public Future<LoginResult> registerThirdParty(String username, String password, ThirdPartySSOUserInfo userInfo, DataChecker dataChecker) {
        return accountRepository.findByUsername(username).flatMap(account -> {
            if (account == null) throw new BizException(ErrorType.Inner.Login, "Account not found");
            var words = PasswordEncodeUtils.encode(password, account.getSalt());
            if (!words.encoded().equals(account.getPassword()))
                throw new BizException(ErrorType.Inner.Login, "Password incorrect");
            return userRepository.findById(account.getUserId());
        }).flatMap(user -> {
            if (user == null) throw new BizException(ErrorType.Inner.Login, "User not found");
            return userInfoRepository.findByUserIdAndPlatformTypes(user.getId(), List.of(userInfo.info().platformType())).flatMap(userInfos -> {
                if (!userInfos.isEmpty()) throw new BizException(ErrorType.Inner.Login, "Login method registered");
                UserInfo register = new UserInfo();
                register.setUser(user);
                var info = userInfo.info();
                register.setMetadata(info.metadata());
                register.setPlatformType(info.platformType());
                register.setDataType(info.dataType());
                return userInfoRepository.insert(register)
                        .map(new LoginResult(user.getId(), user.getName()));
            });
        });
    }

    @Override
    public Future<LoginResult> changePassword(String username, String changed, ThirdPartySSOUserInfo thirdPartyUserInfo, DataChecker dataChecker) {
        return login(thirdPartyUserInfo, dataChecker).flatMap(result -> {
            var encode = PasswordEncodeUtils.encode(changed);
            return accountRepository.updatePasswordByIdAndUsername(result.userId(),username, encode.encoded(),encode.salt())
                    .map(result);
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