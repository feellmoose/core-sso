package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.domain.user.Account;
import com.qingyou.sso.infra.repository.domain.AccountRepository;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {
    private final SqlClient client;


    @Override
    public Future<Account> findByUsername(String username) {
        return client.preparedQuery("SELECT user_id,username,password,salt FROM sso_user.account WHERE username = $1")
                .execute(Tuple.of(username))
                .map(rows -> {
                    for (Row row : rows) {
                        Account account = new Account();
                        account.setUserId(row.getLong("user_id"));
                        account.setUsername(username);
                        account.setPassword(row.getString("password"));
                        account.setSalt(row.getString("salt"));
                        return account;
                    }
                    return null;
                });
    }

    @Override
    public Future<@Nullable Account> insert(Account account) {
        return client.preparedQuery("INSERT INTO sso_user.account(user_id,username,password,salt) VALUES ($1, $2, $3, $4)")
                .execute(Tuple.of(account.getUserId(),account.getUsername(),account.getPassword(),account.getSalt()))
                .map(rows -> account);
    }

    @Override
    public Future<Void> updatePasswordByIdAndUsername(Long userId, String username, String password, String salt) {
        return client.preparedQuery("UPDATE sso_user.account SET password = $1, salt = $2 WHERE user_id = $3 AND username = $4")
                .execute(Tuple.of(password,salt,userId,username)).mapEmpty();
    }

}