package com.qingyou.sso.inject;

import com.qingyou.sso.inject.provider.*;
import com.qingyou.sso.router.admin.AdminRouterHandler;
import com.qingyou.sso.router.global.NotFoundRouterHandler;
import com.qingyou.sso.router.global.SessionRouterHandler;
import com.qingyou.sso.router.oauth.OAuth2RouterHandler;
import com.qingyou.sso.router.sso.EmailSSORouterHandler;
import com.qingyou.sso.router.sso.LoginRouterHandler;
import com.qingyou.sso.router.sso.SSORouterHandlerRegister;
import dagger.Component;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;

import javax.annotation.Nullable;

@Singleton
@Component(modules = {
        ServiceModule.class,
        RepositoryModule.class,
        BaseModule.class,
        RouterHandlerModule.class,
})
public interface RouterHandlerRegisterComponent {
    LoginRouterHandler registerLoginRouters();

    OAuth2RouterHandler registerOauthRouters();

    SessionRouterHandler registerSessionRouters();

    SSORouterHandlerRegister registerSSORouterHandlerRegister();

    AdminRouterHandler registerAdminRouters();

    NotFoundRouterHandler registerNotFoundRouter();

    @Nullable
    EmailSSORouterHandler registerEmailSSORouters();

    RouterGroups registerAllGroups();

    @AllArgsConstructor(onConstructor = @__(@Inject))
    class RouterGroups {
        //session
        private final SessionRouterHandler sessionRouterHandler;
        //login
        private final LoginRouterHandler loginRouterHandler;
        //oauth-service
        private final OAuth2RouterHandler OAuth2RouterHandler;
        //custom-login-module
        private final SSORouterHandlerRegister SSORouterHandlerRegister;
        //email-login-module
        @Nullable
        private final EmailSSORouterHandler emailSSORouterHandler;
        //third-party-app
        private final AdminRouterHandler adminRouterHandler;
    }
}
