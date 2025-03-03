package com.qingyou.sso.service;


import com.qingyou.sso.api.constants.PlatformType;
import com.qingyou.sso.api.result.ThirdPartyAppResult;
import io.vertx.core.Future;

import java.util.List;

public interface ThirdPartyAppService {

    Future<ThirdPartyAppResult> registerApp(String name);

    Future<ThirdPartyAppResult> updateRequiredInfos(String clientId, List<PlatformType> platformTypes);

    Future<ThirdPartyAppResult> updateRedirectURIs(String clientId, List<String> redirectUris);

}
