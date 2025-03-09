package com.qingyou.sso.infra.repository.domain;

import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.domain.oauth.ThirdPartyRequiredUserInfo;
import com.qingyou.sso.domain.user.User;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.List;

public interface ThirdPartyRequiredUserInfoRepository {
    Future<List<ThirdPartyRequiredUserInfo>> findByThirdPartyApp(ThirdPartyApp thirdPartyApp);
    Future<Void> refreshByThirdPartyApp(ThirdPartyApp thirdPartyApp, Collection<ThirdPartyRequiredUserInfo> collection);

    Future<@Nullable ThirdPartyRequiredUserInfo> insert(ThirdPartyRequiredUserInfo info);
}
