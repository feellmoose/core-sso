package com.qingyou.sso.infra.repository.domain.impl;

import com.qingyou.sso.api.constants.DataType;
import com.qingyou.sso.domain.user.User;
import com.qingyou.sso.domain.user.UserInfo;
import com.qingyou.sso.api.constants.PlatformType;
import com.qingyou.sso.infra.repository.domain.UserInfoRepository;
import com.qingyou.sso.utils.StringUtils;
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
public class UserInfoRepositoryImpl implements UserInfoRepository {
    private final SqlClient client;

    @Override
    public Future<List<UserInfo>> findByUserId(Long userId) {
        return client.preparedQuery("SELECT id,metadata,data_type,platform_type FROM sso_user.user_info WHERE user_id = $1")
                .execute(Tuple.of(userId))
                .map(rows -> {
                    List<UserInfo> infos = new ArrayList<>();
                    for (Row row : rows) {
                        UserInfo info = new UserInfo();
                        info.setId(row.getLong("id"));
                        info.setMetadata(row.getString("metadata"));
                        info.setDataType(DataType.values()[row.getInteger("data_type")]);
                        info.setPlatformType(PlatformType.values()[row.getInteger("platform_type")]);
                        infos.add(info);
                    }
                    return infos;
                });
    }

    @Override
    public Future<List<UserInfo>> findByUserIdAndPlatformTypes(Long userId, Collection<PlatformType> platformTypes) {
        return client.preparedQuery("SELECT id,metadata,data_type,platform_type FROM sso_user.user_info WHERE user_id = $1 AND platform_type IN " + StringUtils.union(2, platformTypes))
                .execute(Tuple.of(userId)
                        .addArrayOfInteger(platformTypes.stream()
                                .mapToInt(Enum::ordinal)
                                .boxed()
                                .toArray(Integer[]::new)))
                .map(rows -> {
                    List<UserInfo> infos = new ArrayList<>();
                    for (Row row : rows) {
                        UserInfo info = new UserInfo();
                        info.setId(row.getLong("id"));
                        info.setMetadata(row.getString("metadata"));
                        info.setDataType(DataType.values()[row.getInteger("data_type")]);
                        info.setPlatformType(PlatformType.values()[row.getInteger("platform_type")]);
                        infos.add(info);
                    }
                    return infos;
                });
    }

    @Override
    public Future<@Nullable UserInfo> insert(UserInfo info) {
        return client.preparedQuery("INSERT INTO sso_user.user_info(metadata,data_type,platform_type,user_id) VALUES ($1, $2, $3, $4) RETURNING id")
                .execute(Tuple.of(info.getMetadata(),info.getDataType().ordinal(),info.getPlatformType().ordinal(),info.getUser().getId()))
                .map(rows -> {
                    for (Row row : rows) {
                        info.setId(row.getLong("id"));
                    }
                    return info;
                });
    }

}
