package com.qingyou.sso.inject.provider;

import com.qingyou.sso.infra.repository.domain.*;
import com.qingyou.sso.infra.repository.domain.impl.*;
import dagger.Module;
import dagger.Provides;
import io.vertx.sqlclient.SqlClient;
import jakarta.inject.Singleton;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public AccountRepository provideAccountRepository(SqlClient client) {
        return new AccountRepositoryImpl(client);
    }

    @Provides
    @Singleton
    public UserRepository provideUserRepository(SqlClient client) {
        return new UserRepositoryImpl(client);
    }

    @Provides
    @Singleton
    public UserInfoRepository provideUserInfoRepository(SqlClient client) {
        return new UserInfoRepositoryImpl(client);
    }

    @Provides
    @Singleton
    public ThirdPartyRepository provideThirdPartyRepository(SqlClient client) {
        return new ThirdPartyRepositoryImpl(client);
    }

    @Provides
    @Singleton
    public ThirdPartyRedirectRepository provideThirdPartyRedirectRepository(SqlClient client) {
        return new ThirdPartyRedirectRepositoryImpl(client);
    }

    @Provides
    @Singleton
    public ThirdPartyRequiredUserInfoRepository provideThirdPartyRequiredUserInfoRepository(SqlClient client) {
        return new ThirdPartyRequiredUserInfoRepositoryImpl(client);
    }


}
