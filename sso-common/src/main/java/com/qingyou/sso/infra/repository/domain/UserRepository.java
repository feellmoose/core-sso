package com.qingyou.sso.infra.repository.domain;


import com.qingyou.sso.domain.user.User;
import com.qingyou.sso.infra.repository.base.BaseRepository;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;

public interface UserRepository extends BaseRepository<User> {
    Future<@Nullable User> findByUsername(String username);
    Future<@Nullable User> findByEmail(String email);
    Future<@Nullable User> findByPhone(String phone);
}
