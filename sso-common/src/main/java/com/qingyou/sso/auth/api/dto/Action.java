package com.qingyou.sso.auth.api.dto;

public record Action<O, T>(O owned, T target) implements Info {

    public static <O,T> Action<O,T> required(O owned, T required) {
        return new Action<>(owned, required);
    }

    public static <T> Owned<T> owned(T owned) {
        return new Owned<>(owned);
    }
}
