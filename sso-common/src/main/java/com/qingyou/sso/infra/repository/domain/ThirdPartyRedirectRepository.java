package com.qingyou.sso.infra.repository.domain;

import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.domain.oauth.ThirdPartyRedirect;
import com.qingyou.sso.domain.oauth.ThirdPartyRequiredUserInfo;
import com.qingyou.sso.infra.repository.base.BaseRepository;
import com.qingyou.sso.utils.UniConvertUtils;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.List;

public interface ThirdPartyRedirectRepository extends BaseRepository<ThirdPartyRedirect> {
    Future<List<ThirdPartyRedirect>> findByThirdPartyApp(ThirdPartyApp thirdPartyApp);
    Future<Void> refreshByThirdPartyApp(ThirdPartyApp thirdPartyApp, Collection<ThirdPartyRedirect> collection);
}
