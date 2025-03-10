package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.api.constants.DataType;
import com.qingyou.sso.api.constants.PlatformType;
import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.domain.oauth.ThirdPartyRequiredUserInfo;
import com.qingyou.sso.infra.repository.domain.ThirdPartyRequiredUserInfoRepository;
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
public class ThirdPartyRequiredUserInfoRepositoryImpl implements ThirdPartyRequiredUserInfoRepository {
    private final SqlClient client;

    @Override
    public Future<List<ThirdPartyRequiredUserInfo>> findByThirdPartyApp(ThirdPartyApp thirdPartyApp) {
        return client.preparedQuery("SELECT id,data_type,platform_type FROM sso_oauth.third_party_required_user_info WHERE third_party_app_id = $1")
                .execute(Tuple.of(thirdPartyApp.getId()))
                .map(rows -> {
                    List<ThirdPartyRequiredUserInfo> infos = new ArrayList<>();
                    for (Row row : rows) {
                        ThirdPartyRequiredUserInfo info = new ThirdPartyRequiredUserInfo();
                        info.setThirdPartyApp(thirdPartyApp);
                        info.setId(row.getLong("id"));
                        info.setDataType(DataType.values()[row.getInteger("data_type")]);
                        info.setPlatformType(PlatformType.values()[row.getInteger("platform_type")]);
                    }
                    return infos;
                });
    }

    @Override
    public Future<Void> refreshByThirdPartyApp(ThirdPartyApp thirdPartyApp, Collection<ThirdPartyRequiredUserInfo> collection) {
        return client.preparedQuery("DELETE FROM sso_oauth.third_party_required_user_info WHERE third_party_app_id = $1")
                .execute(Tuple.of(thirdPartyApp.getId())).flatMap(rows -> {
                    return client.preparedQuery("INSERT INTO sso_oauth.third_party_required_user_info(data_type,platform_type,third_party_app_id) VALUES ($1, $2, $3) RETURNING id")
                            .executeBatch(collection.stream()
                                    .map(info -> Tuple.of(info.getDataType().ordinal(),info.getPlatformType().ordinal(),info.getThirdPartyApp().getId()))
                                    .toList())
                            .map(r -> {
                                var i = r.iterator();
                                for(var info: collection){
                                    if (i.hasNext()){
                                        info.setId(i.next().getLong("id"));
                                    }
                                }
                                return null;
                            });
                }).mapEmpty();
    }

    @Override
    public Future<@Nullable ThirdPartyRequiredUserInfo> insert(ThirdPartyRequiredUserInfo info) {
        return client.preparedQuery("INSERT INTO sso_oauth.third_party_required_user_info(data_type,platform_type,third_party_app_id) VALUES ($1, $2, $3) RETURNING id")
                .execute(Tuple.of(info.getDataType().ordinal(),info.getPlatformType().ordinal(),info.getThirdPartyApp().getId()))
                .map(rows -> {
                    for(Row row: rows){
                        info.setId(row.getLong("id"));
                    }
                    return info;
                });
    }


}
