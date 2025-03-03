package com.qingyou.sso.infra.exception;

import lombok.AllArgsConstructor;

public sealed interface ErrorType permits ErrorType.Inner, ErrorType.Showed, ErrorType.OAuth2 {
    String message();

    int code();

    @AllArgsConstructor
    enum Inner implements ErrorType {
        Init("Service init", 0),
        Default("Default error", 0),
        Login("Login failed", 5001),
        ;
        private final String message;
        private final int code;

        @Override
        public String message() {
            return message;
        }

        @Override
        public int code() {
            return code;
        }
    }

    @AllArgsConstructor
    enum Showed implements ErrorType {
        Params("Params invalid", 1001),
        Auth("Auth check failed", 1002),
        ;
        private final String message;
        private final int code;

        @Override
        public String message() {
            return message;
        }

        @Override
        public int code() {
            return code;
        }

    }

    @AllArgsConstructor
    enum OAuth2 implements ErrorType {
        INVALID_GRANT("invalid_grant", "Invalid or expired grant", 400),
        INVALID_REQUEST("invalid_request", "Invalid request parameters", 400),
        INVALID_RESPONSE_TYPE("invalid_response_type", "Invalid response type", 400),
        INVALID_SCOPE("invalid_scope", "Invalid or unauthorized scope", 400),
        INVALID_REDIRECT_URI("invalid_redirect_uri", "Invalid redirect URI", 400),
        UNSUPPORTED_GRANT_TYPE("unsupported_grant_type", "Unsupported grant type", 400),
        INVALID_ACCOUNT("invalid_account", "Invalid or unauthorized account", 400),

        INVALID_CLIENT("invalid_client", "Client authentication failed", 401),

        UNAUTHORIZED_CLIENT("unauthorized_client", "Unauthorized client for this grant type", 403),
        ACCESS_DENIED("access_denied", "Resource access denied", 403),
        INSUFFICIENT_SCOPE("insufficient_scope", "Insufficient scope for this resource", 403),
        INTERACTION_REQUIRED("interaction_required", "User interaction required", 403),
        LOGIN_REQUIRED("login_required", "User login required", 403),
        CONSENT_REQUIRED("consent_required", "User consent required", 403),
        ACCOUNT_SELECTION_REQUIRED("account_selection_required", "Account selection required", 403),

        SERVER_ERROR("server_error", "Unexpected server error", 500),

        TEMPORARILY_UNAVAILABLE("temporarily_unavailable", "Service temporarily unavailable", 503);;

        private final String error;
        private final String description;
        private final String errorURI;
        private final int code;

        OAuth2(String error, String description, int code) {
            this.error = error;
            this.description = description;
            this.code = code;
            this.errorURI = null;
        }

        public String description() {
            return description;
        }

        public String error() {
            return error;
        }

        public String errorUri() {
            return errorURI;
        }

        @Override
        public String message() {
            return error;
        }

        @Override
        public int code() {
            return code;
        }

        public static OAuth2 of(String error, String description) {
            for (OAuth2 o : values()) {
                if (o.error().equals(error) && o.description().equals(description)) {
                    return o;
                }
            }
            return null;
        }
    }

}
