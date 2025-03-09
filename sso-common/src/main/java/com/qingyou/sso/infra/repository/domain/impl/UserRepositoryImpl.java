package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.domain.user.User;
import com.qingyou.sso.infra.repository.domain.UserRepository;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private SqlClient client;

    @Override
    public Future<@Nullable User> findById(Object id) {
        return client.preparedQuery("SELECT (id,name,email,phone) FROM sso_user.user WHERE id = ?")
                .execute(Tuple.of(id))
                .map(rows -> {
                    for (Row row : rows) {
                        User user = new User();
                        user.setId(row.getLong("id"));
                        user.setName(row.getString("name"));
                        user.setEmail(row.getString("email"));
                        user.setPhone(row.getString("phone"));
                        return user;
                    }
                    return null;
                });
    }

    @Override
    public Future<User> findByUsername(String username) {
        return client.preparedQuery("SELECT (id,name,email,phone) FROM sso_user.user WHERE id in (SELECT user_id FROM sso_user.account WHERE username = ?)")
                .execute(Tuple.of(username))
                .map(rows -> {
                    for (Row row : rows) {
                        User user = new User();
                        user.setId(row.getLong("id"));
                        user.setName(row.getString("name"));
                        user.setEmail(row.getString("email"));
                        user.setPhone(row.getString("phone"));
                        return user;
                    }
                    return null;
                });
    }

    @Override
    public Future<@Nullable User> findByEmail(String email) {
        return client.preparedQuery("SELECT (id,name,email,phone) FROM sso_user.user WHERE email = ?")
                .execute(Tuple.of(email))
                .map(rows -> {
                    for (Row row : rows) {
                        User user = new User();
                        user.setId(row.getLong("id"));
                        user.setName(row.getString("name"));
                        user.setEmail(row.getString("email"));
                        user.setPhone(row.getString("phone"));
                        return user;
                    }
                    return null;
                });
    }

    @Override
    public Future<@Nullable User> findByPhone(String phone) {
        return client.preparedQuery("SELECT (id,name,email,phone) FROM sso_user.user WHERE phone = ?")
                .execute(Tuple.of(phone))
                .map(rows -> {
                    for (Row row : rows) {
                        User user = new User();
                        user.setId(row.getLong("id"));
                        user.setName(row.getString("name"));
                        user.setEmail(row.getString("email"));
                        user.setPhone(row.getString("phone"));
                        return user;
                    }
                    return null;
                });
    }

    @Override
    public Future<@Nullable User> insert(User user) {
        return client.preparedQuery("INSERT INTO sso_user.user(name,email,phone) VALUES (?, ?, ?) RETURNING id")
                .execute(Tuple.of(user.getName(),user.getEmail(),user.getPhone()))
                .map(rows -> {
                    for (Row row : rows) {
                        user.setId(row.getLong("id"));
                    }
                    return user;
                });
    }
}
