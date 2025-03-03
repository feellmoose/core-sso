package com.qingyou.sso.inject.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyou.sso.auth.api.AuthService;
import com.qingyou.sso.auth.api.dto.AuthServiceWithEventBus;
import com.qingyou.sso.api.dto.BaseDependency;
import com.qingyou.sso.infra.cache.Cache;
import com.qingyou.sso.infra.cache.RedisCache;
import com.qingyou.sso.infra.config.Configuration;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.ext.web.sstore.redis.RedisSessionStore;
import io.vertx.redis.client.Redis;
import jakarta.inject.Singleton;
import org.hibernate.reactive.mutiny.Mutiny;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


@Module
public class BaseModule extends BaseDependency {

    public BaseModule(Configuration configuration, Mutiny.SessionFactory sessionFactory, Redis redis, ObjectMapper objectMapper, Vertx vertx) {
        this.configuration = configuration;
        this.sessionFactory = sessionFactory;
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.vertx = vertx;
    }

    private final Redis redis;

    @Provides
    @Singleton
    public SessionStore sessionStore(Redis redis) {
        return RedisSessionStore.create(vertx, redis);
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
    public Redis redis() {
        return redis;
    }

    @Provides
    @Singleton
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Provides
    @Singleton
    public Cache cache() {
        cache = new RedisCache(redis, Duration.of(configuration.redis().cache().ttl(), ChronoUnit.MILLIS));
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
