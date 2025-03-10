package com.qingyou.sso.infra.repository.domain;


import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;

public interface ThirdPartyRepository {
    Future<ThirdPartyApp> findById(Long id);
    Future<ThirdPartyApp> findByClientId(String clientId);

    Future<@Nullable ThirdPartyApp> insert(ThirdPartyApp app);
}
