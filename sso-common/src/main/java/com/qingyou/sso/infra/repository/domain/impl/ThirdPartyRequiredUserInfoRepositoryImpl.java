package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.domain.oauth.ThirdPartyRequiredUserInfo;
import com.qingyou.sso.infra.repository.base.BaseRepositoryImpl;
import com.qingyou.sso.infra.repository.domain.ThirdPartyRequiredUserInfoRepository;
import com.qingyou.sso.utils.UniConvertUtils;
import io.vertx.core.Future;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.Collection;
import java.util.List;

public class ThirdPartyRequiredUserInfoRepositoryImpl extends BaseRepositoryImpl<ThirdPartyRequiredUserInfo> implements ThirdPartyRequiredUserInfoRepository {
    public ThirdPartyRequiredUserInfoRepositoryImpl(Mutiny.SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Future<List<ThirdPartyRequiredUserInfo>> findByThirdPartyApp(ThirdPartyApp thirdPartyApp) {
        return sessionFactory.withSession(session -> {
            return session.createQuery("from ThirdPartyRequiredUserInfo where thirdPartyApp = :thirdPartyApp", ThirdPartyRequiredUserInfo.class)
                    .setParameter("thirdPartyApp",thirdPartyApp)
                    .getResultList();
        }).convert().with(UniConvertUtils::toFuture);
    }

    @Override
    public Future<Void> refreshByThirdPartyApp(ThirdPartyApp thirdPartyApp, Collection<ThirdPartyRequiredUserInfo> collection) {
        return sessionFactory.withSession(session -> {
            return session.removeAll(thirdPartyApp.getRequiredUserInfos().toArray())
                    .chain(thirdPartyRedirects -> session.mergeAll(collection.toArray()))
                    .chain(session::flush);
        }).convert().with(UniConvertUtils::toFuture);
    }
}
