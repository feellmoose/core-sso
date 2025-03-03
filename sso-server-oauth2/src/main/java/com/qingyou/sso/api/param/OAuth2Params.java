package com.qingyou.sso.api.param;

import com.qingyou.sso.infra.exception.ErrorType;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.ext.web.RoutingContext;

import java.net.URI;

public interface OAuth2Params {
    void inject(RoutingContext routingContext);

    record ValidationResult<T>(
            @Nullable T result,
            ErrorType.OAuth2 error
    ){
        public boolean success(){
            return error == null;
        }
        public static <T> ValidationResult<T> result(T result) {
            return new ValidationResult<>(result, null);
        }
        public static <T> ValidationResult<T> fail(ErrorType.OAuth2 error) {
            return new ValidationResult<>(null, error);
        }
    }

    record Authorization(
            String clientId,
            String redirectURI,
            ResponseType responseType,
            @Nullable String codeChallenge,
            @Nullable String codeChallengeMethod,
            @Nullable String scope,
            @Nullable String state
    ) {
    }

    enum ResponseType {
        code,
        token
    }

    enum GrantType {
        authorization_code,
        refresh_token,
        client_credentials,
    }

    record Info(
            String accessToken
    ) {

    }

    /**
     * Combine refresh & verify
     */
    record Token(
            GrantType grantType,
            String clientId,
            String clientSecret,
            @Nullable String refreshToken,
            @Nullable String redirectURI,
            @Nullable String code,
            @Nullable String codeVerifier
    ){

        public ValidationResult<Credentials> credentials() {
            return ValidationResult.result(new Credentials(grantType, clientId, clientSecret));
        }

        public ValidationResult<Refresh> refresh(){
            if(refreshToken == null) return ValidationResult.fail(ErrorType.OAuth2.INVALID_REQUEST);
            return ValidationResult.result(new Refresh(grantType,clientId,clientSecret,refreshToken));
        }

        public ValidationResult<Verify> verify(){
            if(redirectURI == null || code == null) return ValidationResult.fail(ErrorType.OAuth2.INVALID_REQUEST);
            try {
                URI uri = URI.create(redirectURI);
                if (uri.getHost() == null || uri.getHost().isEmpty())
                    return ValidationResult.fail(ErrorType.OAuth2.INVALID_REDIRECT_URI);
                if (uri.getScheme() == null
                        || (!uri.getScheme().equalsIgnoreCase("http") && !uri.getScheme().equalsIgnoreCase("https"))
                ) return ValidationResult.fail(ErrorType.OAuth2.INVALID_REDIRECT_URI);
            } catch (IllegalArgumentException ex) {
                return ValidationResult.fail(ErrorType.OAuth2.INVALID_REDIRECT_URI);
            }
            return ValidationResult.result(new Verify(grantType,clientId,clientSecret,redirectURI,code,codeVerifier));
        }

    }

    record Credentials(
            GrantType grantType,
            String clientId,
            String clientSecret
    ){}

    record Refresh(
            GrantType grantType,
            String clientId,
            String clientSecret,
            String refreshToken
    ) {

    }

    record Verify(
            GrantType grantType,
            String clientId,
            String clientSecret,
            String redirectURI,
            String code,
            @Nullable String codeVerifier
    ) {

    }

    record Password(
            String clientId,
            String clientSecret,
            String username,
            String password,
            @Nullable String scope
    ){

    }

}
