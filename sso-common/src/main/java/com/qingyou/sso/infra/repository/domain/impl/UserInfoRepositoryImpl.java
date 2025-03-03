package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.domain.user.UserInfo;
import com.qingyou.sso.api.constants.PlatformType;
import com.qingyou.sso.infra.repository.base.BaseRepositoryImpl;
import com.qingyou.sso.infra.repository.domain.UserInfoRepository;
import com.qingyou.sso.utils.UniConvertUtils;
import io.vertx.core.Future;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.Collection;
import java.util.List;


public class UserInfoRepositoryImpl extends BaseRepositoryImpl<UserInfo> implements UserInfoRepository {

    public UserInfoRepositoryImpl(Mutiny.SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Future<List<UserInfo>> findByUserId(Long userId) {
        return sessionFactory.withSession(session -> session.createQuery("from UserInfo where user.id = :userId", UserInfo.class)
                .setParameter("userId", userId)
                .getResultList()
        ).convert().with(UniConvertUtils::toFuture);
    }

    @Override
    public Future<List<UserInfo>> findByUserIdAndPlatformTypes(Long userId, Collection<PlatformType> platformTypes) {
        return sessionFactory.withSession(session -> session.createQuery("from UserInfo where user.id = :userId and platformType in :platformTypes", UserInfo.class)
                .setParameter("userId", userId)
                .setParameter("platformTypes", platformTypes)
                .getResultList()
        ).convert().with(UniConvertUtils::toFuture);
    }
}
