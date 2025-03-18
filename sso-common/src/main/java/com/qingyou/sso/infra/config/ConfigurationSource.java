package com.qingyou.sso.infra.config;

import io.vertx.core.json.JsonObject;
import lombok.Getter;

public class ConfigurationSource{
    private volatile Configuration configuration;
    @Getter
    private final JsonObject source;

    public ConfigurationSource(JsonObject source) {
        this.source = source;
    }

    public Configuration getConfiguration() {
        if (this.configuration == null) {
            synchronized (source) {
                if (this.configuration == null) {
                    this.configuration = source.mapTo(Configuration.class);
                }
            }
        }
        return configuration;
    }
}
