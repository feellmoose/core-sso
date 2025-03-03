package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.domain.user.Account;
import com.qingyou.sso.infra.repository.base.BaseRepositoryImpl;
import com.qingyou.sso.infra.repository.domain.AccountRepository;
import com.qingyou.sso.utils.UniConvertUtils;
import io.vertx.core.Future;
import org.hibernate.reactive.common.Identifier;
import org.hibernate.reactive.mutiny.Mutiny;

public class AccountRepositoryImpl extends BaseRepositoryImpl<Account> implements AccountRepository {

    public AccountRepositoryImpl(Mutiny.SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Future<Account> findByUsername(String username) {
        return sessionFactory.withSession(session ->
                session.find(Account.class, Identifier.id("username", username))
        ).convert().with(UniConvertUtils::toFuture);
    }

}
