package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.domain.oauth.ThirdPartyRedirect;
import com.qingyou.sso.infra.repository.base.BaseRepositoryImpl;
import com.qingyou.sso.infra.repository.domain.ThirdPartyRedirectRepository;
import com.qingyou.sso.utils.UniConvertUtils;
import io.vertx.core.Future;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.Collection;
import java.util.List;

public class ThirdPartyRedirectRepositoryImpl extends BaseRepositoryImpl<ThirdPartyRedirect> implements ThirdPartyRedirectRepository {

    public ThirdPartyRedirectRepositoryImpl(Mutiny.SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Future<List<ThirdPartyRedirect>> findByThirdPartyApp(ThirdPartyApp thirdPartyApp) {
        return sessionFactory.withSession(session -> {
            return session.createQuery("from ThirdPartyRedirect where thirdPartyApp = :thirdPartyApp", ThirdPartyRedirect.class)
                    .setParameter("thirdPartyApp",thirdPartyApp)
                    .getResultList();
        }).convert().with(UniConvertUtils::toFuture);
    }

    @Override
    public Future<Void> refreshByThirdPartyApp(ThirdPartyApp thirdPartyApp, Collection<ThirdPartyRedirect> collection) {
        return sessionFactory.withSession(session -> {
            return session.removeAll(thirdPartyApp.getRedirectURIs().toArray())
                    .chain(thirdPartyRedirects -> session.mergeAll(collection.toArray()))
                    .chain(session::flush);
        }).convert().with(UniConvertUtils::toFuture);
    }

}
