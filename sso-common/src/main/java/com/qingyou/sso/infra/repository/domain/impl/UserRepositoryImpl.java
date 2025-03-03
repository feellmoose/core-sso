package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.domain.user.User;
import com.qingyou.sso.infra.repository.base.BaseRepositoryImpl;
import com.qingyou.sso.infra.repository.domain.UserRepository;
import com.qingyou.sso.utils.UniConvertUtils;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import org.hibernate.reactive.mutiny.Mutiny;


public class UserRepositoryImpl extends BaseRepositoryImpl<User> implements UserRepository {

    public UserRepositoryImpl(Mutiny.SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Future<@Nullable User> findByUsername(String username) {
        return sessionFactory.withSession(session ->
                session.createQuery("from User where User.account.username= :username", User.class)
                        .setParameter("username", username)
                        .getSingleResultOrNull()
        ).convert().with(UniConvertUtils::toFuture);
    }
}
