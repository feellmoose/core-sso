package com.qingyou.sso.auth.api.dto;

import com.qingyou.sso.auth.internal.rbac.RbacUserInfo;
import com.qingyou.sso.auth.internal.rbac.TargetInfo;

public record Action(RbacUserInfo owned, TargetInfo target) implements Info {

    public static Action required(RbacUserInfo owned, TargetInfo required) {
        return new Action(owned, required);
    }
}
