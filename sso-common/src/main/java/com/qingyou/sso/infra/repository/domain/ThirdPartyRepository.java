package com.qingyou.sso.infra.repository.domain;


import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.infra.repository.base.BaseRepository;
import io.vertx.core.Future;

public interface ThirdPartyRepository extends BaseRepository<ThirdPartyApp> {

    Future<ThirdPartyApp> findByClientId(String clientId);
}
