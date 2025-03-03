package com.qingyou.sso.router.admin;

import com.qingyou.sso.api.ThirdPartyAppManagement;
import com.qingyou.sso.handler.AuthHandler;
import com.qingyou.sso.handler.ThirdPartyAppManagementService;
import com.qingyou.sso.infra.response.GlobalHttpResponse;
import com.qingyou.sso.service.ThirdPartyAppService;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminRouterHandler implements Handler<Router> {
    private final ThirdPartyAppManagement thirdPartyAppManagement;
    private final AuthHandler.Factory factory;

    public AdminRouterHandler(ThirdPartyAppService thirdPartyAppService, AuthHandler.Factory factory) {
        this.factory = factory;
        this.thirdPartyAppManagement = new ThirdPartyAppManagementService(thirdPartyAppService);
    }

    @Override
    public void handle(Router router) {
        router.route("/admin/*").handler(BodyHandler.create());
        router.post("/admin/app/register")
                .handler(factory.target("add","app").build())
                .handler(GlobalHttpResponse.wrap(thirdPartyAppManagement::create, log));
        router.post("/admin/app/info/update")
                .handler(factory.target("update","app").build())
                .handler(GlobalHttpResponse.wrap(thirdPartyAppManagement::updateRequiredInfos, log));
        router.post("/admin/app/redirect/update")
                .handler(factory.target("update","app").build())
                .handler(GlobalHttpResponse.wrap(thirdPartyAppManagement::updateRedirectURIs, log));
    }
}
