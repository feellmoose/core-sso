package com.qingyou.sso.inject.provider;

import com.qingyou.sso.api.dto.BaseDependency;
import com.qingyou.sso.auth.api.AuthService;
import com.qingyou.sso.auth.api.dto.AuthServiceWithEventBus;
import com.qingyou.sso.infra.cache.Cache;
import com.qingyou.sso.infra.config.ConfigurationSource;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.sqlclient.SqlClient;
import jakarta.inject.Singleton;


@Module
public class BaseModule extends BaseDependency {

    public BaseModule(ConfigurationSource configurationSource, Vertx vertx, SqlClient sqlClient, Cache cache) {
        this.configuration = configurationSource;
        this.vertx = vertx;
        this.cache = cache;
        this.sqlClient = sqlClient;
    }

    @Provides
    @Singleton
    public SessionStore sessionStore() {
        if (vertx.isClustered()) ClusteredSessionStore.create(vertx);
        return LocalSessionStore.create(vertx);
    }

    @Provides
    @Singleton
    public SqlClient sqlClient() {
        return sqlClient;
    }

    @Provides
    @Singleton
    public ConfigurationSource configuration() {
        return configuration;
    }

    @Provides
    @Singleton
    public Cache cache() {
        return this.cache;
    }

    @Provides
    @Singleton
    public WebClient webClient() {
        webClient = WebClient.create(vertx);
        return webClient;
    }

    @Provides
    @Singleton
    public MailClient provideMailClient() {
        var mail = configuration.getSource().getJsonObject("mail");
        if (mail == null) return null;
        return MailClient.create(vertx, new MailConfig(mail));
    }

    @Provides
    @Singleton
    public AuthService authService() {
        return new AuthServiceWithEventBus(vertx.eventBus());
    }

}
