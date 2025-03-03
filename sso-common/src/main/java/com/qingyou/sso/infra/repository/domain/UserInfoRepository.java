package com.qingyou.sso.infra.repository.domain;

import com.qingyou.sso.domain.user.UserInfo;
import com.qingyou.sso.api.constants.PlatformType;
import com.qingyou.sso.infra.repository.base.BaseRepository;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.List;

public interface UserInfoRepository extends BaseRepository<UserInfo> {

    Future<List<UserInfo>> findByUserId(Long userId);
    Future<List<UserInfo>> findByUserIdAndPlatformTypes(Long userId, Collection<PlatformType> platformTypes);

}
