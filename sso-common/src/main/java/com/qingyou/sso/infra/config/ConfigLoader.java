package com.qingyou.sso.infra.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class ConfigLoader {
    private final Vertx vertx;

    public Future<Configuration> load() {
        ConfigRetriever retriever = ConfigRetriever.create(vertx, defaultOptions());
        return retriever.getConfig().map(json ->
                json.mapTo(Configuration.class)
        );
    }

    public Future<ConfigurationSource> loadSource() {
        ConfigRetriever retriever = ConfigRetriever.create(vertx, defaultOptions());
        return retriever.getConfig().map(ConfigurationSource::new);
    }

    private static ConfigRetrieverOptions defaultOptions() {
        return new ConfigRetrieverOptions()
                .setIncludeDefaultStores(true)
                .setScanPeriod(-1)
                .addStore(new ConfigStoreOptions()
                        .setType("file")
                        .setFormat("json")
                        .setOptional(true)
                        .setConfig(new JsonObject().put("path", "verticles.json")))
                .addStore(new ConfigStoreOptions()
                        .setType("file")
                        .setFormat("json")
                        .setOptional(true)
                        .setConfig(new JsonObject().put("path", "conf.json")));
    }

}
