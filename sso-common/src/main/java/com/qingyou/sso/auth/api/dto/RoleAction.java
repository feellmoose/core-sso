package com.qingyou.sso.auth.api.dto;

import com.qingyou.sso.auth.internal.rbac.TargetInfo;
import com.qingyou.sso.domain.auth.TargetRole;
import com.qingyou.sso.domain.auth.UserRole;
import com.qingyou.sso.domain.user.UserInfo;

import java.util.List;

public record RoleAction(List<UserRole> owned, TargetRole target)  {

    public static RoleAction required(List<UserRole> owned, TargetRole target) {
        return new RoleAction(owned, target);
    }
}