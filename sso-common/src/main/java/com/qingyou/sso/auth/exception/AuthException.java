package com.qingyou.sso.auth.exception;

public class AuthException extends RuntimeException {

    public AuthException(String reason) {
        super("Auth error: " + reason);
    }

    public AuthException(Throwable cause, String reason) {
        super("Auth error: " + reason, cause);
    }

}
