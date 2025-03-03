package com.qingyou.sso.handler;

import com.qingyou.sso.api.ThirdPartyAppManagement;
import com.qingyou.sso.api.param.AppCreate;
import com.qingyou.sso.api.param.RedirectURIsUpdate;
import com.qingyou.sso.api.param.RequiredInfosUpdate;
import com.qingyou.sso.api.result.ThirdPartyAppResult;
import com.qingyou.sso.service.ThirdPartyAppService;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public class ThirdPartyAppManagementService implements ThirdPartyAppManagement {

    private final ThirdPartyAppService thirdPartyAppService;

    public ThirdPartyAppManagementService(ThirdPartyAppService thirdPartyAppService) {
        this.thirdPartyAppService = thirdPartyAppService;
    }

    @Override
    public Future<ThirdPartyAppResult> create(RoutingContext routingContext) {
        AppCreate appCreate = routingContext.body().asPojo(AppCreate.class);
        return thirdPartyAppService.registerApp(appCreate.name());
    }

    @Override
    public Future<ThirdPartyAppResult> updateRequiredInfos(RoutingContext routingContext) {
        RequiredInfosUpdate requiredInfosUpdate = routingContext.body().asPojo(RequiredInfosUpdate.class);
        return thirdPartyAppService.updateRequiredInfos(requiredInfosUpdate.clientId(), requiredInfosUpdate.platformTypes());
    }

    @Override
    public Future<ThirdPartyAppResult> updateRedirectURIs(RoutingContext routingContext) {
        RedirectURIsUpdate redirectURIsUpdate = routingContext.body().asPojo(RedirectURIsUpdate.class);
        return thirdPartyAppService.updateRedirectURIs(redirectURIsUpdate.clientId(), redirectURIsUpdate.redirectURIs());
    }
}
