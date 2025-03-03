package com.qingyou.sso.serviece;


import com.qingyou.sso.api.param.OAuth2Params;
import com.qingyou.sso.api.result.AccessToken;
import com.qingyou.sso.api.result.AuthorizationCode;
import com.qingyou.sso.api.result.ResourceData;
import com.qingyou.sso.api.result.UserInfo;
import io.vertx.core.Future;


public interface OAuth2Service {
    Future<AuthorizationCode> authorization(
            Long userId,
            OAuth2Params.Authorization authorization
    );

    Future<AccessToken> verify(
            OAuth2Params.Verify verify
    );

    Future<AccessToken> implicitAuthorization(
            Long userId,
            OAuth2Params.Authorization authorization
    );

    Future<AccessToken> refresh(
            OAuth2Params.Refresh refresh
    );

    Future<ResourceData<UserInfo>> info(
            OAuth2Params.Info info
    );

    Future<AccessToken> password(
            OAuth2Params.Password password
    );
}
