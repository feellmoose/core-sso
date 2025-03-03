package com.qingyou.sso.api.dto;

public record Result<T>(
        boolean success,
        int code,
        String message,
        T data
) {

    public static <T> Result<T> success(T data) {
        return new Result<>(true, 0, "", data);
    }

    public static <T> Result<T> failed(int code, String message) {
        return new Result<>(false, code, message, null);
    }

}
