package com.qingyou.sso.api.dto;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyou.sso.infra.cache.Cache;
import com.qingyou.sso.infra.config.Configuration;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import lombok.Getter;
import org.hibernate.reactive.mutiny.Mutiny;

@Getter
public abstract class BaseDependency {
    protected Vertx vertx;
    protected Configuration configuration;
    protected Mutiny.SessionFactory sessionFactory;
    protected Cache cache;
    protected ObjectMapper objectMapper;
    protected WebClient webClient;
}