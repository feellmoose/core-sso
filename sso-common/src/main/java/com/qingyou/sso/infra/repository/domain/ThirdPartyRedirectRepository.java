package com.qingyou.sso.infra.repository.domain;

import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.domain.oauth.ThirdPartyRedirect;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.List;

public interface ThirdPartyRedirectRepository {
    Future<List<ThirdPartyRedirect>> findByThirdPartyApp(ThirdPartyApp thirdPartyApp);
    Future<Void> refreshByThirdPartyApp(ThirdPartyApp thirdPartyApp, Collection<ThirdPartyRedirect> collection);

    Future<@Nullable ThirdPartyRedirect> insert(ThirdPartyRedirect thirdPartyRedirect);
}
