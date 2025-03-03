package com.qingyou.sso.auth.api.dto;

public record Owned<O>(O owned) implements Info {

    public static <T> Owned<T> owned(T owned) {
        return new Owned<>(owned);
    }

    public <T> Action<O,T> required(T target) {
        return new Action<>(owned, target);
    }

    public Checked<O> checked() {
        return new Checked<>(owned);
    }

}
