package com.qingyou.sso.inject.provider;

import com.qingyou.sso.infra.repository.domain.*;
import com.qingyou.sso.infra.repository.domain.impl.*;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import org.hibernate.reactive.mutiny.Mutiny;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public AccountRepository provideAccountRepository(Mutiny.SessionFactory sessionFactory) {
        return new AccountRepositoryImpl(sessionFactory);
    }

    @Provides
    @Singleton
    public UserRepository provideUserRepository(Mutiny.SessionFactory sessionFactory) {
        return new UserRepositoryImpl(sessionFactory);
    }

    @Provides
    @Singleton
    public UserInfoRepository provideUserInfoRepository(Mutiny.SessionFactory sessionFactory) {
        return new UserInfoRepositoryImpl(sessionFactory);
    }

    @Provides
    @Singleton
    public ThirdPartyRepository provideThirdPartyRepository(Mutiny.SessionFactory sessionFactory) {
        return new ThirdPartyRepositoryImpl(sessionFactory);
    }

    @Provides
    @Singleton
    public ThirdPartyRedirectRepository provideThirdPartyRedirectRepository(Mutiny.SessionFactory sessionFactory) {
        return new ThirdPartyRedirectRepositoryImpl(sessionFactory);
    }

    @Provides
    @Singleton
    public ThirdPartyRequiredUserInfoRepository provideThirdPartyRequiredUserInfoRepository(Mutiny.SessionFactory sessionFactory) {
        return new ThirdPartyRequiredUserInfoRepositoryImpl(sessionFactory);
    }


}
