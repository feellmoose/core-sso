package com.qingyou.sso.infra.repository.domain;

import com.qingyou.sso.domain.user.Account;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;

public interface AccountRepository{

    Future<Account> findByUsername(String username);

    Future<@Nullable Account> insert(Account account);

    Future<Void> updatePasswordByIdAndUsername(Long userId, String username, String password, String salt);

}
