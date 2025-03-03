package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.infra.repository.base.BaseRepositoryImpl;
import com.qingyou.sso.infra.repository.domain.ThirdPartyRepository;
import com.qingyou.sso.utils.UniConvertUtils;
import io.vertx.core.Future;
import org.hibernate.reactive.mutiny.Mutiny;

public class ThirdPartyRepositoryImpl extends BaseRepositoryImpl<ThirdPartyApp> implements ThirdPartyRepository {

    public ThirdPartyRepositoryImpl(Mutiny.SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Future<ThirdPartyApp> findByClientId(String clientId) {
        return sessionFactory.withSession(session -> session.createQuery("from ThirdPartyApp where clientId = :clientId", ThirdPartyApp.class)
                .setParameter("clientId", clientId)
                .getSingleResultOrNull()
        ).convert().with(UniConvertUtils::toFuture);
    }
}
