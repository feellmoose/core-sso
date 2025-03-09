package com.qingyou.sso.api.dto;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyou.sso.infra.cache.Cache;
import com.qingyou.sso.infra.config.Configuration;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.sqlclient.SqlClient;
import lombok.Getter;

@Getter
public abstract class BaseDependency {
    protected Vertx vertx;
    protected Configuration configuration;
    protected Cache cache;
    protected ObjectMapper objectMapper;
    protected WebClient webClient;
    protected SqlClient sqlClient;
}