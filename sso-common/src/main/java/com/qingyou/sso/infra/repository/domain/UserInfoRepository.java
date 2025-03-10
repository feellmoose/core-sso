package com.qingyou.sso.infra.repository.domain;

import com.qingyou.sso.api.constants.PlatformType;
import com.qingyou.sso.domain.user.UserInfo;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.List;

public interface UserInfoRepository {

    Future<List<UserInfo>> findByUserId(Long userId);
    Future<List<UserInfo>> findByUserIdAndPlatformTypes(Long userId, Collection<PlatformType> platformTypes);

    Future<@Nullable UserInfo> insert(UserInfo info);
}
