package com.qingyou.sso.auth.api.dto;

public sealed interface Info permits Checked, Action, Owned {

    static <T> Checked<T> checked(T info) {
        return new Checked<>(info);
    }

    static <O, T> Action<O, T> required(O owned, T required) {
        return new Action<>(owned, required);
    }

    static <T> Owned<T> owned(T owned) {
        return new Owned<>(owned);
    }

}


