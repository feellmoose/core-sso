package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.infra.repository.domain.ThirdPartyRepository;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ThirdPartyRepositoryImpl implements ThirdPartyRepository {

    private final SqlClient client;

    @Override
    public Future<ThirdPartyApp> findById(Long id) {
        return client.preparedQuery("SELECT (id,app_name,client_id,client_secret) FROM sso_oauth.third_party_app WHERE id = ?")
                .execute(Tuple.of(id))
                .map(rows -> {
                    for (Row row: rows){
                        ThirdPartyApp app = new ThirdPartyApp();
                        app.setId(row.getLong("id"));
                        app.setClientId(row.getString("client_id"));
                        app.setAppName(row.getString("app_name"));
                        app.setClientSecret(row.getString("client_secret"));
                        return app;
                    }
                    return null;
                });
    }

    @Override
    public Future<ThirdPartyApp> findByClientId(String clientId) {
        return client.preparedQuery("SELECT (id,app_name,client_id,client_secret) FROM sso_oauth.third_party_app WHERE client_id = ?")
                .execute(Tuple.of(clientId))
                .map(rows -> {
                    for (Row row: rows){
                        ThirdPartyApp app = new ThirdPartyApp();
                        app.setId(row.getLong("id"));
                        app.setClientId(row.getString("client_id"));
                        app.setAppName(row.getString("app_name"));
                        app.setClientSecret(row.getString("client_secret"));
                        return app;
                    }
                    return null;
                });
    }

    @Override
    public Future<@Nullable ThirdPartyApp> insert(ThirdPartyApp app) {
        return client.preparedQuery("INSERT INTO sso_oauth.third_party_app(app_name,client_id,client_secret) VALUES (?,?,?) RETURNING id")
                .execute(Tuple.of(app.getAppName(),app.getClientId(),app.getClientSecret()))
                .map(rows -> {
                    for (Row row: rows){
                        app.setId(row.getLong("id"));
                    }
                    return app;
                });
    }
}