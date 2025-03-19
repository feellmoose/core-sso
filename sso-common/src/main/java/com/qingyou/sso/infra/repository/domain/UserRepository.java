package com.qingyou.sso.infra.repository.domain;


import com.qingyou.sso.domain.user.User;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;

public interface UserRepository {
    Future<@Nullable User> findById(Object id);
    Future<@Nullable User> findByUsername(String username);
    Future<@Nullable User> findByName(String name);
    Future<@Nullable User> findByEmail(String email);
    Future<@Nullable User> findByPhone(String phone);
    Future<@Nullable User> insert(User user);
}
