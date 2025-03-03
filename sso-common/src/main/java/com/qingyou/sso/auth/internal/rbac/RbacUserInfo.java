package com.qingyou.sso.auth.internal.rbac;


public record RbacUserInfo(
        Long id,
        String name
) implements IRbac.IUser {
}