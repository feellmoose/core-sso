package com.qingyou.sso.service;

import com.qingyou.sso.api.constants.DataType;
import com.qingyou.sso.api.constants.PlatformType;
import com.qingyou.sso.api.result.ThirdPartyAppResult;
import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.domain.oauth.ThirdPartyRedirect;
import com.qingyou.sso.domain.oauth.ThirdPartyRequiredUserInfo;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.infra.repository.domain.ThirdPartyRedirectRepository;
import com.qingyou.sso.infra.repository.domain.ThirdPartyRepository;
import com.qingyou.sso.infra.repository.domain.ThirdPartyRequiredUserInfoRepository;
import io.vertx.core.Future;
import lombok.AllArgsConstructor;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class ThirdPartyAppServiceImpl implements ThirdPartyAppService {

    private final ThirdPartyRepository thirdPartyRepository;
    private final ThirdPartyRedirectRepository thirdPartyRedirectRepository;
    private final ThirdPartyRequiredUserInfoRepository thirdPartyRequiredUserInfoRepository;

    @Override
    public Future<ThirdPartyAppResult> registerApp(String name) {
        byte[] bytes = new byte[256];
        new SecureRandom().nextBytes(bytes);
        ThirdPartyApp app = new ThirdPartyApp();
        app.setAppName(name);
        app.setClientId(UUID.randomUUID().toString());
        app.setClientSecret(Base64.getEncoder().encodeToString(bytes));
        return thirdPartyRepository.insert(app).map(v ->
                new ThirdPartyAppResult(app.getAppName(),app.getClientId(),app.getClientSecret(), Collections.emptyList(), Collections.emptyList())
        );
    }

    @Override
    public Future<ThirdPartyAppResult> updateRequiredInfos(String clientId, List<PlatformType> platformTypes) {
        return thirdPartyRepository.findByClientId(clientId)
                .flatMap(app -> {
                    if (app == null) throw new BizException(ErrorType.Showed.Params,"Client id not exist");
                    List<ThirdPartyRequiredUserInfo> thirdPartyRequiredUserInfos = platformTypes.stream().map(platformType -> {
                        ThirdPartyRequiredUserInfo requiredUserInfo = new ThirdPartyRequiredUserInfo();
                        requiredUserInfo.setThirdPartyApp(app);
                        requiredUserInfo.setDataType(DataType.Json);
                        requiredUserInfo.setPlatformType(platformType);
                        return requiredUserInfo;
                    }).toList();
                    return thirdPartyRequiredUserInfoRepository
                            .refreshByThirdPartyApp(app,thirdPartyRequiredUserInfos)
                            .flatMap(v -> thirdPartyRepository.findById(app.getId()));
                }).map(app -> new ThirdPartyAppResult(
                        app.getAppName(),
                        app.getClientId(),
                        app.getClientSecret(),
                        app.getRequiredUserInfos().stream()
                                .map(ThirdPartyRequiredUserInfo::getPlatformType)
                                .toList(),
                        app.getRedirectURIs().stream()
                                .map(ThirdPartyRedirect::getURI)
                                .toList())
                );
    }

    @Override
    public Future<ThirdPartyAppResult> updateRedirectURIs(String clientId, List<String> redirectUris) {
        return thirdPartyRepository.findByClientId(clientId)
                .flatMap(app -> {
                    if (app == null) throw new BizException(ErrorType.Showed.Params,"Client id not exist");
                    List<ThirdPartyRedirect> redirects = redirectUris.stream()
                            .map(redirectURI -> {
                                try {
                                    URI uri = URI.create(redirectURI);
                                    if (uri.getHost() == null || uri.getHost().isEmpty())
                                        throw new BizException(ErrorType.OAuth2.INVALID_REDIRECT_URI, "URI validate failed");
                                    if (uri.getScheme() == null
                                            || (!uri.getScheme().equalsIgnoreCase("http") && !uri.getScheme().equalsIgnoreCase("https"))
                                    )  throw new BizException(ErrorType.OAuth2.INVALID_REDIRECT_URI, "URI validate failed");
                                } catch (IllegalArgumentException ex) {
                                    throw new BizException(ErrorType.OAuth2.INVALID_REDIRECT_URI, "URI validate failed");
                                }
                                ThirdPartyRedirect redirect = new ThirdPartyRedirect();
                                redirect.setThirdPartyApp(app);
                                redirect.setURI(redirectURI);
                                return redirect;
                            }).toList();
                    return thirdPartyRedirectRepository
                            .refreshByThirdPartyApp(app, redirects)
                            .flatMap(v -> thirdPartyRepository.findById(app.getId()));
                }).map(app -> new ThirdPartyAppResult(
                        app.getAppName(),
                        app.getClientId(),
                        app.getClientSecret(),
                        app.getRequiredUserInfos().stream()
                                .map(ThirdPartyRequiredUserInfo::getPlatformType)
                                .toList(),
                        app.getRedirectURIs().stream()
                                .map(ThirdPartyRedirect::getURI)
                                .toList())
                );
    }

}
