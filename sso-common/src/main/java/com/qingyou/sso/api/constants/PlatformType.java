package com.qingyou.sso.api.constants;

import io.vertx.codegen.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public enum PlatformType {
    Email("email"),
    Phone("phone"),
    ;

    private final String obj;

    PlatformType(String obj) {
        this.obj = obj;
    }

    public String obj() {
        return obj;
    }

    public String scope() {
        return "info:" + obj;
    }

    private final static Set<PlatformType> values = Arrays.stream(PlatformType.values()).collect(Collectors.toSet());

    public static Set<PlatformType> fromObj(@Nullable String obj) {
        if (obj.equals("all")) return values;
        for (PlatformType platformType : PlatformType.values()) {
            if (platformType.obj.equals(obj)) {
                return Collections.singleton(platformType);
            }
        }
        return Collections.emptySet();
    }

    public static Set<PlatformType> fromObjs(@Nullable List<String> objs) {
        Set<PlatformType> result = new HashSet<>();
        for (String obj : objs) {
            if (obj.equals("all")) return values;
            for (PlatformType platformType : PlatformType.values()) {
                if (platformType.obj.equals(obj)) {
                    result.add(platformType);
                }
            }
        }
        return result;
    }

    public static Set<PlatformType> fromScope(@Nullable String scope) {
        if (scope.equals("info:all")) return values;
        for (PlatformType platformType : PlatformType.values()) {
            if (platformType.scope().equals(scope)) {
                return Collections.singleton(platformType);
            }
        }
        return Collections.emptySet();
    }

}
