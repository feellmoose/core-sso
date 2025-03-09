package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.domain.oauth.ThirdPartyRedirect;
import com.qingyou.sso.infra.repository.domain.ThirdPartyRedirectRepository;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class ThirdPartyRedirectRepositoryImpl implements ThirdPartyRedirectRepository {

    private final SqlClient client;

    @Override
    public Future<List<ThirdPartyRedirect>> findByThirdPartyApp(ThirdPartyApp thirdPartyApp) {
        return client.preparedQuery("SELECT(id,uri) FROM sso_oauth.third_party_redirect WHERE third_party_app_id = ?")
                .execute(Tuple.of(thirdPartyApp.getId()))
                .map(rows -> {
                    List<ThirdPartyRedirect> thirdPartyRedirects = new ArrayList<>();
                    for (Row row : rows) {
                        ThirdPartyRedirect redirect = new ThirdPartyRedirect();
                        redirect.setThirdPartyApp(thirdPartyApp);
                        redirect.setId(row.getLong("id"));
                        redirect.setURI(row.getString("uri"));
                        thirdPartyRedirects.add(redirect);
                    }
                    return thirdPartyRedirects;
                });
    }

    @Override
    public Future<Void> refreshByThirdPartyApp(ThirdPartyApp thirdPartyApp, Collection<ThirdPartyRedirect> collection) {
        return client.preparedQuery("DELETE FROM sso_oauth.third_party_redirect WHERE third_party_app_id = ?")
                .execute(Tuple.of(thirdPartyApp.getId())).flatMap(rows -> {
                    return client.preparedQuery("INSERT INTO sso_oauth.third_party_redirect(uri,third_party_app_id) VALUES (?,?) RETURNING id")
                            .executeBatch(collection.stream()
                                    .map(thirdPartyRedirect -> Tuple.of(thirdPartyRedirect.getURI(),thirdPartyRedirect.getThirdPartyApp().getId()))
                                    .toList())
                            .map(r -> {
                                var i = r.iterator();
                                for(var thirdPartyRedirect: collection){
                                    if (i.hasNext()){
                                        thirdPartyRedirect.setId(i.next().getLong("id"));
                                    }
                                }
                                return null;
                            });
                }).mapEmpty();
    }

    @Override
    public Future<@Nullable ThirdPartyRedirect> insert(ThirdPartyRedirect thirdPartyRedirect) {
        return client.preparedQuery("INSERT INTO sso_oauth.third_party_redirect(id,uri,third_party_app_id) VALUES (?,?,?) RETURNING id")
                .execute(Tuple.of(thirdPartyRedirect.getId(),thirdPartyRedirect.getURI(),thirdPartyRedirect.getThirdPartyApp().getId()))
                        .map(rows -> {
                            for(Row row: rows){
                                thirdPartyRedirect.setId(row.getLong("id"));
                            }
                            return thirdPartyRedirect;
                        });
    }

}
