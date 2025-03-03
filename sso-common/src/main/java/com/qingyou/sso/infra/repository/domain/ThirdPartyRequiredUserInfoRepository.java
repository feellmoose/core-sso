package com.qingyou.sso.infra.repository.domain;

import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.domain.oauth.ThirdPartyRequiredUserInfo;
import com.qingyou.sso.infra.repository.base.BaseRepository;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.List;

public interface ThirdPartyRequiredUserInfoRepository extends BaseRepository<ThirdPartyRequiredUserInfo> {
    Future<List<ThirdPartyRequiredUserInfo>> findByThirdPartyApp(ThirdPartyApp thirdPartyApp);
    Future<Void> refreshByThirdPartyApp(ThirdPartyApp thirdPartyApp, Collection<ThirdPartyRequiredUserInfo> collection);
}
