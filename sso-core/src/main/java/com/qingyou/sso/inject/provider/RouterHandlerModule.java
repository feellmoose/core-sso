package com.qingyou.sso.inject.provider;

import com.qingyou.sso.handler.AuthHandler;
import com.qingyou.sso.handler.platform.EmailSSOHandler;
import com.qingyou.sso.infra.config.ConfigurationSource;
import com.qingyou.sso.router.admin.AdminRouterHandler;
import com.qingyou.sso.router.global.NotFoundRouterHandler;
import com.qingyou.sso.router.global.SessionRouterHandler;
import com.qingyou.sso.router.oauth.OAuth2RouterHandler;
import com.qingyou.sso.router.sso.*;
import com.qingyou.sso.service.BaseSSOService;
import com.qingyou.sso.service.EmailSSOService;
import com.qingyou.sso.service.ThirdPartyAppService;
import com.qingyou.sso.serviece.OAuth2Service;
import dagger.Module;
import dagger.Provides;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.sstore.SessionStore;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;

import javax.annotation.Nullable;

@Module
@AllArgsConstructor
public class RouterHandlerModule {

    private final Router router;

    @Provides
    @Singleton
    SessionRouterHandler provideSessionRouterHandler(SessionStore sessionStore, ConfigurationSource configurationSource) {
        var handler = new SessionRouterHandler(sessionStore, configurationSource);
        handler.handle(router);
        return handler;
    }

    @Provides
    @Singleton
    public EmailSSORouterHandler provideEmailSSORouterHandler(@Nullable EmailSSOService emailSSOService) {
        if (emailSSOService == null) return null;
        var handler = new EmailSSORouterHandler(new EmailSSOHandler(emailSSOService));
        handler.handle(router);
        return handler;
    }

    @Provides
    @Singleton
    LoginRouterHandler provideLoginRouterHandler(BaseSSOService baseSSOService) {
        var handler = new LoginRouterHandler(baseSSOService);
        handler.handle(router);
        return handler;
    }

    @Provides
    @Singleton
    OAuth2RouterHandler provideOauthRouterHandler(OAuth2Service oAuth2Service) {
        var handler = new OAuth2RouterHandler(oAuth2Service);
        handler.handle(router);
        return handler;
    }

    @Provides
    @Singleton
    SSORouterHandlerRegistry provideCustomRouterHandlerRegister(BaseSSOService baseSSOService) {
        SSORouterHandlerRegistry registry = new SSORouterHandlerRegistry(baseSSOService);
        for (CustomSSORouterHandler handler: registry.getAll()){
            handler.handle(router);
        }
        return registry;
    }

    @Provides
    @Singleton
    NotFoundRouterHandler provideNotFoundRouterHandler() {
        var handler = new NotFoundRouterHandler();
        handler.handle(router);
        return handler;
    }

    @Provides
    @Singleton
    AdminRouterHandler provideAppRouterHandler(ThirdPartyAppService thirdPartyAppService, AuthHandler.Factory factory) {
        var handler = new AdminRouterHandler(thirdPartyAppService, factory);
        handler.handle(router);
        return handler;
    }

}
