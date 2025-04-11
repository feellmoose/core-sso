package com.qingyou.sso.auth.api.dto;

import com.qingyou.sso.auth.internal.rbac.RbacUserInfo;
import com.qingyou.sso.auth.internal.rbac.TargetInfo;
import com.qingyou.sso.domain.user.UserInfo;
import io.vertx.ext.auth.User;

import java.util.List;

public record Action(RbacUserInfo owned, TargetInfo target) {

    public static Action required(RbacUserInfo owned, TargetInfo required) {
        return new Action(owned, required);
    }
}



