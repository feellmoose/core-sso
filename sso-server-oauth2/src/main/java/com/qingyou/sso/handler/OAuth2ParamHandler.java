package com.qingyou.sso.handler;

import com.qingyou.sso.api.param.OAuth2Params;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.infra.response.OAuth2HttpResponse;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public abstract class OAuth2ParamHandler<T> implements OAuth2Params {

    @Override
    public void inject(RoutingContext routingContext) {
        try {
            OAuth2Params.ValidationResult<?> result = this.getFrom(routingContext);
            routingContext.put("params", result);
            routingContext.next();
        } catch (BizException ex) {
            OAuth2HttpResponse.fail(routingContext,ex);
        }
    }

    protected abstract ValidationResult<T> getFrom(RoutingContext routingContext);

    public static OAuth2ParamHandler<Authorization> authorization(){
        return new AuthorizationParam();
    }
    public static OAuth2ParamHandler<Info> info(){
        return new InfoParam();
    }
    public static OAuth2ParamHandler<Refresh> refresh(){
        return new RefreshParam();
    }
    public static OAuth2ParamHandler<Verify> verify(){
        return new VerifyParam();
    }
    public static OAuth2ParamHandler<Token> token(){
        return new TokenParam();
    }

    public static OAuth2ParamHandler<Password> password(){
        return new PasswordParam();
    }

    public static class AuthorizationParam extends OAuth2ParamHandler<Authorization> {
        @Override
        protected ValidationResult<Authorization> getFrom(RoutingContext routingContext) {
            ValidationResult<Long> auth = ssoSession(routingContext);
            if (!auth.success()) return ValidationResult.fail(auth.error());

            MultiMap params = routingContext.queryParams();

            if (!params.contains("client_id")
                    || !params.contains("redirect_uri")
                    || !params.contains("response_type")
            ) return ValidationResult.fail(ErrorType.OAuth2.INVALID_REQUEST);

            String clientId = params.get("client_id");
            String redirectURI = params.get("redirect_uri");

            String codeChallengeMethod = params.get("code_challenge_method");
            String codeChallenge = params.get("code_challenge");
            String scope = Optional.ofNullable(params.get("scope")).orElse("all");
            String state = params.get("state");

            ResponseType responseType = switch (params.get("response_type")) {
                case "token" -> ResponseType.token;
                case "code" -> ResponseType.code;
                default -> null;
            };
            if (responseType == null) return ValidationResult.fail(ErrorType.OAuth2.INVALID_RESPONSE_TYPE);
            if (clientId.length() > 40) return ValidationResult.fail(ErrorType.OAuth2.INVALID_CLIENT);

            //URI param verify
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

            if (!scope.equals("all")
                    && Arrays.stream(scope.split(" ")).anyMatch(string -> string.split(":").length != 2))
                return ValidationResult.fail(ErrorType.OAuth2.INVALID_SCOPE);

            if (state != null && state.length() > 10) return ValidationResult.fail(ErrorType.OAuth2.INVALID_REQUEST);

            if (codeChallengeMethod != null) {
                //PKCE param verify
                if (!codeChallengeMethod.equalsIgnoreCase("s256"))
                    return ValidationResult.fail(ErrorType.OAuth2.INVALID_REQUEST);
                if (codeChallenge == null || codeChallenge.length() != 43)
                    return ValidationResult.fail(ErrorType.OAuth2.INVALID_REQUEST);
            }

            return ValidationResult.result(new Authorization(clientId, redirectURI, responseType, codeChallenge, codeChallengeMethod, scope, state));
        }
    }

    public static class VerifyParam extends OAuth2ParamHandler<Verify> {
        @Override
        protected ValidationResult<Verify> getFrom(RoutingContext routingContext) {
            var auth = basicAuth(routingContext);
            if (!auth.success()) return ValidationResult.fail(auth.error());
            String[] words = auth.result();

            MultiMap params = routingContext.queryParams();

            if (!params.contains("client_id")
                    || !params.contains("grant_type")
                    || !params.contains("redirect_uri")
                    || !params.contains("code")
            ) return ValidationResult.fail(ErrorType.OAuth2.INVALID_REQUEST);

            String codeVerifier = params.get("code_verifier");
            String clientId = params.get("client_id");
            String redirectURI = params.get("redirect_uri");
            String code = params.get("code");

            GrantType grantType = switch (params.get("grant_type")) {
                case "authorization_code" -> GrantType.authorization_code;
                case "refresh_token" -> GrantType.refresh_token;
                default -> null;
            };
            if (grantType != GrantType.authorization_code) return ValidationResult.fail(ErrorType.OAuth2.INVALID_GRANT);

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

            if (clientId.length() > 40
                    || !words[0].equals(clientId)
            ) return ValidationResult.fail(ErrorType.OAuth2.INVALID_CLIENT);

            return ValidationResult.result(new Verify(grantType, clientId, words[1], redirectURI, code, codeVerifier));
        }
    }

    public static class TokenParam extends OAuth2ParamHandler<Token> {
        @Override
        protected ValidationResult<Token> getFrom(RoutingContext routingContext) {
            var auth = basicAuth(routingContext);
            if (!auth.success()) return ValidationResult.fail(auth.error());
            String[] words = auth.result();

            MultiMap params = routingContext.queryParams();

            if (!params.contains("client_id")
                    || !params.contains("grant_type")
            ) return ValidationResult.fail(ErrorType.OAuth2.INVALID_REQUEST);

            String refreshToken = params.get("refresh_token");
            String codeVerifier = params.get("code_verifier");
            String clientId = params.get("client_id");
            String redirectURI = params.get("redirect_uri");
            String code = params.get("code");

            GrantType grantType = switch (params.get("grant_type")) {
                case "authorization_code" -> GrantType.authorization_code;
                case "refresh_token" -> GrantType.refresh_token;
                case "client_credentials" -> GrantType.client_credentials;
                default -> null;
            };
            if (grantType == null) return ValidationResult.fail(ErrorType.OAuth2.INVALID_GRANT);

            if (clientId.length() > 40
                    || !words[0].equals(clientId)
            ) return ValidationResult.fail(ErrorType.OAuth2.INVALID_CLIENT);

            return ValidationResult.result(new Token(
                    grantType,
                    clientId,
                    words[1],
                    refreshToken,
                    redirectURI,
                    code, codeVerifier)
            );
        }
    }

    public static class InfoParam extends OAuth2ParamHandler<Info> {
        @Override
        protected ValidationResult<Info> getFrom(RoutingContext routingContext) {
            var auth = bearerToken(routingContext);
            if (!auth.success()) return ValidationResult.fail(auth.error());
            String token = auth.result();
            return ValidationResult.result(new Info(token));
        }
    }

    public static class RefreshParam extends OAuth2ParamHandler<Refresh> {
        @Override
        protected ValidationResult<Refresh> getFrom(RoutingContext routingContext) {
            var auth = basicAuth(routingContext);
            if (!auth.success()) return ValidationResult.fail(auth.error());
            String[] words = auth.result();

            MultiMap params = routingContext.queryParams();

            if (!params.contains("client_id")
                    || !params.contains("grant_type")
                    || !params.contains("refresh_token")
            ) return ValidationResult.fail(ErrorType.OAuth2.INVALID_REQUEST);

            String clientId = params.get("client_id");
            String refreshToken = params.get("refresh_token");

            GrantType grantType = switch (params.get("grant_type")) {
                case "authorization_code" -> GrantType.authorization_code;
                case "refresh_token" -> GrantType.refresh_token;
                default -> null;
            };
            if (grantType != GrantType.refresh_token) return ValidationResult.fail(ErrorType.OAuth2.INVALID_GRANT);

            if (clientId.length() > 40
                    || !words[0].equals(clientId)
            ) return ValidationResult.fail(ErrorType.OAuth2.INVALID_CLIENT);

            return ValidationResult.result(new Refresh(grantType, clientId, words[1], refreshToken));
        }
    }

    public static class PasswordParam extends OAuth2ParamHandler<Password> {
        @Override
        protected ValidationResult<Password> getFrom(RoutingContext routingContext) {
            var auth = basicAuth(routingContext);
            if (!auth.success()) return ValidationResult.fail(auth.error());
            String[] words = auth.result();
            String username = routingContext.body().asJsonObject().getString("username");
            String password = routingContext.body().asJsonObject().getString("password");
            String scope = routingContext.body().asJsonObject().getString("scope");
            if (username == null || password == null) {
                return ValidationResult.fail(ErrorType.OAuth2.INVALID_REQUEST);
            }
            return ValidationResult.result(new Password(words[0], words[1], username, password, scope));
        }
    }

    public static ValidationResult<String[]> basicAuth(RoutingContext routingContext){
        String authorization = routingContext.request().getHeader("Authorization");
        if (authorization == null
                || !authorization.startsWith("Basic")
        ) return ValidationResult.fail(ErrorType.OAuth2.ACCESS_DENIED);

        byte[] base64 = Base64.getDecoder().decode(authorization.substring(6));
        String[] words = new String(base64, StandardCharsets.UTF_8).split(":");
        if (words.length != 2) return ValidationResult.fail(ErrorType.OAuth2.ACCESS_DENIED);

        return ValidationResult.result(words);
    }

    public static ValidationResult<String> bearerToken(RoutingContext routingContext){
        String authorization = routingContext.request().getHeader("Authorization");
        if (authorization == null
                || !authorization.startsWith("Bearer")
        ) return ValidationResult.fail(ErrorType.OAuth2.ACCESS_DENIED);

        String token = authorization.substring(7);
        return ValidationResult.result(token);
    }

    public static ValidationResult<Long> ssoSession(RoutingContext routingContext){
        //require login before oauth(session with userId)
        Session session = routingContext.session();
        if (session == null) return ValidationResult.fail(ErrorType.OAuth2.LOGIN_REQUIRED);
        Long userId = session.get("userId");
        if (userId == null) return ValidationResult.fail(ErrorType.OAuth2.LOGIN_REQUIRED);
        return ValidationResult.result(userId);
    }


}
