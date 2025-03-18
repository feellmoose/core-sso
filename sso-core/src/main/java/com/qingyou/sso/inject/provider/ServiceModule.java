package com.qingyou.sso.inject.provider;

import com.qingyou.sso.auth.api.AuthService;
import com.qingyou.sso.handler.AuthHandler;
import com.qingyou.sso.infra.cache.Cache;
import com.qingyou.sso.infra.config.Configuration;
import com.qingyou.sso.infra.config.ConfigurationSource;
import com.qingyou.sso.infra.repository.domain.*;
import com.qingyou.sso.service.*;
import com.qingyou.sso.serviece.DefaultOAuth2Service;
import com.qingyou.sso.serviece.OAuth2Service;
import dagger.Module;
import dagger.Provides;
import io.vertx.ext.mail.MailClient;
import jakarta.inject.Singleton;

import javax.annotation.Nullable;

@Module
public class ServiceModule {

    @Provides
    @Singleton
    public BaseSSOService provideLoginService(UserRepository userRepository, AccountRepository accountRepository) {
        return new DefaultBaseSSOService(userRepository, accountRepository);
    }

    @Provides
    @Singleton
    public OAuth2Service provideOauthServiceService(AuthService authService, UserInfoRepository userInfoRepository, UserRepository userRepository, ThirdPartyRepository thirdPartyRepository, ConfigurationSource configurationSource, Cache cache) {
        return new DefaultOAuth2Service(authService, thirdPartyRepository, userInfoRepository, userRepository, configurationSource, cache);
    }

    @Provides
    @Singleton
    public ThirdPartyAppService partyAppService(ThirdPartyRepository thirdPartyRepository, ThirdPartyRedirectRepository thirdPartyRedirectRepository, ThirdPartyRequiredUserInfoRepository thirdPartyRequiredUserInfoRepository) {
        return new ThirdPartyAppServiceImpl(thirdPartyRepository, thirdPartyRedirectRepository, thirdPartyRequiredUserInfoRepository);
    }

    @Provides
    @Singleton
    public AuthHandler.Factory provideAuthHandlerFactory(AuthService authService, UserRepository userRepository) {
        return new AuthHandler.Factory(userRepository, authService);
    }

    @Provides
    @Singleton
    public EmailSSOService provideEmailSSOService(@Nullable MailClient mailClient, ConfigurationSource configurationSource, Cache cache, UserRepository userRepository, AccountRepository accountRepository) {
        if (mailClient == null) return null;
        return new DefaultEmailSSOService(mailClient, configurationSource, cache, userRepository, accountRepository);
    }

}
