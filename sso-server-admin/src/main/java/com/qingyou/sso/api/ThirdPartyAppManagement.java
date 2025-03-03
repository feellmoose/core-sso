package com.qingyou.sso.api;

import com.qingyou.sso.api.result.ThirdPartyAppResult;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;


public interface ThirdPartyAppManagement {
    Future<ThirdPartyAppResult> create(RoutingContext routingContext);
    Future<ThirdPartyAppResult> updateRequiredInfos(RoutingContext routingContext);
    Future<ThirdPartyAppResult> updateRedirectURIs(RoutingContext routingContext);
}
