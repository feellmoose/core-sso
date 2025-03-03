package com.qingyou.sso.auth.internal.rbac;

public record TargetInfo(
        Long appId,
        String action,
        String object
) implements IRbac.ITarget {
}