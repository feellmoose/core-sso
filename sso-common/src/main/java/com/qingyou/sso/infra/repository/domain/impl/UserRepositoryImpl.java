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
        return client.preparedQuery("SELECT id,name,email,phone FROM sso_user.user WHERE id = $1")
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
        return client.preparedQuery("SELECT id,name,email,phone FROM sso_user.user WHERE id IN (SELECT user_id FROM sso_user.account WHERE username = $1)")
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
    public Future<@Nullable User> findByName(String name) {
        return client.preparedQuery("SELECT id,name,email,phone FROM sso_user.user WHERE name = $1")
                .execute(Tuple.of(name))
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
        return client.preparedQuery("SELECT id,name,email,phone FROM sso_user.user WHERE email = $1")
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
        return client.preparedQuery("SELECT id,name,email,phone FROM sso_user.user WHERE phone = $1")
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
        return client.preparedQuery("INSERT INTO sso_user.user(name,email,phone) VALUES ($1, $2, $3) RETURNING id")
                .execute(Tuple.of(user.getName(),user.getEmail(),user.getPhone()))
                .map(rows -> {
                    for (Row row : rows) {
                        user.setId(row.getLong("id"));
                    }
                    return user;
                });
    }
}
