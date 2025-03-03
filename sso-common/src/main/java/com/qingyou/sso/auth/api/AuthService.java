package com.qingyou.sso.auth.api;

import com.qingyou.sso.auth.api.dto.Action;
import com.qingyou.sso.api.dto.Result;
import com.qingyou.sso.auth.internal.rbac.TargetInfo;
import com.qingyou.sso.auth.internal.rbac.RbacUserInfo;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.List;


public interface AuthService {

    Future<Result<Void>> rbac(Action<RbacUserInfo, TargetInfo> action);
    Future<Result<Void>> rbac(Collection<Action<RbacUserInfo, TargetInfo>> actions);
}
