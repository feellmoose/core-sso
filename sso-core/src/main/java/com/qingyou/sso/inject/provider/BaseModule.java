package com.qingyou.sso.inject.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyou.sso.api.dto.BaseDependency;
import com.qingyou.sso.auth.api.AuthService;
import com.qingyou.sso.auth.api.dto.AuthServiceWithEventBus;
import com.qingyou.sso.infra.cache.Cache;
import com.qingyou.sso.infra.cache.ClusterCache;
import com.qingyou.sso.infra.config.Configuration;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import jakarta.inject.Singleton;
import org.hibernate.reactive.mutiny.Mutiny;


@Module
public class BaseModule extends BaseDependency {

    public BaseModule(Configuration configuration, Mutiny.SessionFactory sessionFactory, ObjectMapper objectMapper, Vertx vertx) {
        this.configuration = configuration;
        this.sessionFactory = sessionFactory;
        this.objectMapper = objectMapper;
        this.vertx = vertx;
    }

    @Provides
    @Singleton
    public SessionStore sessionStore() {
        return ClusteredSessionStore.create(vertx);
    }

    @Provides
    @Singleton
    public Configuration configuration() {
        return configuration;
    }

    @Provides
    @Singleton
    public Mutiny.SessionFactory sessionFactory() {
        return sessionFactory;
    }

    @Provides
    @Singleton
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Provides
    @Singleton
    public Cache cache() {
        cache = new ClusterCache(vertx);
        return cache;
    }

    @Provides
    @Singleton
    public WebClient webClient() {
        webClient = WebClient.create(vertx);
        return webClient;
    }

    @Provides
    @Singleton
    public AuthService authService() {
        return new AuthServiceWithEventBus(vertx.eventBus(), objectMapper);
    }

}
