package com.qingyou.sso.service;

import com.qingyou.sso.api.param.Code;
import com.qingyou.sso.api.param.Email;
import com.qingyou.sso.api.param.UsernamePassword;
import com.qingyou.sso.api.result.LoginResult;
import com.qingyou.sso.domain.user.Account;
import com.qingyou.sso.domain.user.User;
import com.qingyou.sso.infra.cache.Cache;
import com.qingyou.sso.infra.config.Configuration;
import com.qingyou.sso.infra.config.ConfigurationSource;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.infra.repository.domain.AccountRepository;
import com.qingyou.sso.infra.repository.domain.UserRepository;
import com.qingyou.sso.utils.PasswordEncodeUtils;
import com.qingyou.sso.utils.StringUtils;
import io.vertx.core.Future;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;

import java.time.Duration;

public class DefaultEmailSSOService implements EmailSSOService {
    private final MailClient mailClient;
    private final Configuration.Mail.Message config;
    private final Cache cache;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public DefaultEmailSSOService(MailClient mailClient, ConfigurationSource configurationSource, Cache cache, UserRepository userRepository, AccountRepository accountRepository) {
        this.mailClient = mailClient;
        this.config = configurationSource.getConfiguration().mail().message();
        this.cache = cache;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }


    @Override
    public Future<Boolean> email(Email email) {
        String code = StringUtils.randomNum(5);
        return cache.set(code, email, Duration.ofMillis(config.expire())).flatMap(v ->
                mailClient.sendMail(new MailMessage(config.from(), email.email() ,config.subject(),config.pattern().formatted(code)))
                        .map(mailResult -> mailResult.getMessageID() != null && !mailResult.getMessageID().isEmpty())
        );
    }

    @Override
    public Future<LoginResult> login(Code code) {
        return cache.get(code.code(), String.class).flatMap(email -> {
            if (email == null) throw new BizException(ErrorType.Showed.Auth,"Code expire or login failed");
            else {
                return userRepository.findByEmail(email).map(user -> {
                    if (user == null) throw new BizException(ErrorType.Showed.Auth,"Please register first");
                    return new LoginResult(user.getId(),user.getName());
                });
            }
        });
    }

    @Override
    public Future<LoginResult> register(Code code) {
        return cache.get(code.code(), String.class).flatMap(email -> {
            if (email == null) throw new BizException(ErrorType.Showed.Auth,"Code expire or login failed");
            else {
                return userRepository.findByEmail(email).flatMap(user -> {
                    if (user != null) throw new BizException(ErrorType.Showed.Auth,"Register failed, please login");
                    User register = new User();
                    register.setEmail(email);
                    register.setName(email);
                    return userRepository.insert(register);
                }).flatMap(user -> {
                    return accountRepository.findByUsername(code.username()).map(account -> {
                        if (account != null) throw new BizException(ErrorType.Showed.Auth,"Account of this username already exists");
                        return null;
                    }).flatMap(v -> {
                        var encoded = PasswordEncodeUtils.encode(code.password());
                        Account account = new Account(
                                user.getId(),
                                code.username() == null? user.getEmail() : code.username(),
                                encoded.encoded(),encoded.salt(),
                                user);
                        return accountRepository.insert(account).map(user);
                    });
                }).map(user ->  new LoginResult(user.getId(),user.getName()));
            }
        });
    }
}
