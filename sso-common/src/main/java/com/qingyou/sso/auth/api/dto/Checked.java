package com.qingyou.sso.auth.api.dto;

public record Checked<T>(T info) implements Info {
    public static <T> Checked<T> checked(T info) {
        return new Checked<>(info);
    }
}
