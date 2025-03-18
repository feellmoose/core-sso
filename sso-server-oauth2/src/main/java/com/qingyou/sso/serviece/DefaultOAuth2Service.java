package com.qingyou.sso.serviece;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.qingyou.sso.api.constants.PlatformType;
import com.qingyou.sso.api.param.OAuth2Params;
import com.qingyou.sso.api.result.AccessToken;
import com.qingyou.sso.api.result.AuthorizationCode;
import com.qingyou.sso.api.result.ResourceData;
import com.qingyou.sso.api.result.UserInfo;
import com.qingyou.sso.auth.api.AuthService;
import com.qingyou.sso.auth.api.dto.Action;
import com.qingyou.sso.auth.internal.rbac.RbacUserInfo;
import com.qingyou.sso.auth.internal.rbac.TargetInfo;
import com.qingyou.sso.domain.oauth.ThirdPartyApp;
import com.qingyou.sso.domain.oauth.ThirdPartyRequiredUserInfo;
import com.qingyou.sso.domain.user.User;
import com.qingyou.sso.infra.cache.Cache;
import com.qingyou.sso.infra.config.ConfigurationSource;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.infra.repository.domain.ThirdPartyRepository;
import com.qingyou.sso.infra.repository.domain.UserInfoRepository;
import com.qingyou.sso.infra.repository.domain.UserRepository;
import com.qingyou.sso.utils.PasswordEncodeUtils;
import com.qingyou.sso.utils.StringUtils;
import io.vertx.core.Future;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultOAuth2Service implements OAuth2Service {

    private final AuthService authService;
    private final ThirdPartyRepository thirdPartyRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserRepository userRepository;
    private final Cache cache;

    private final JWTVerifier verifier;
    private final Algorithm algorithm;

    private final Duration codeExpireTime = Duration.of(10, ChronoUnit.MINUTES);
    private final Duration accessTokenExpireTime = Duration.of(60, ChronoUnit.SECONDS);
    private final long accessTokenExpireIn = accessTokenExpireTime.toMillis();
    private final Duration refreshTokenExpireTime = Duration.of(1, ChronoUnit.DAYS);

    public DefaultOAuth2Service(AuthService authService, ThirdPartyRepository thirdPartyRepository, UserInfoRepository userInfoRepository, UserRepository userRepository, ConfigurationSource configuration, Cache cache) {
        this.authService = authService;
        this.thirdPartyRepository = thirdPartyRepository;
        this.userInfoRepository = userInfoRepository;
        this.userRepository = userRepository;
        this.cache = cache;
        this.algorithm = Algorithm.HMAC512(configuration.getConfiguration().security().jwt().secret());
        this.verifier = JWT.require(algorithm).build();
    }

    //verify scope
    private Future<Void> verifyScope(User user, ThirdPartyApp app, String scope) {
        if (scope == null || scope.isBlank()) throw new BizException(ErrorType.Showed.Params, "Scope not exists");
        if (scope.equals("all")) {
            //Require all permissions; Go for rbac system and verifying.
            return authService.rbac(Action.required(new RbacUserInfo(user.getId(), user.getName()), new TargetInfo(app.getId(), "all", "all"))).map(v -> {
                if (!v.success()) throw new BizException(ErrorType.Showed.Params, "Auth error, rbac auth failed");
                return null;
            });
        } else {
            String[] scopes = scope.split(" ");
            List<String[]> infos = new ArrayList<>();
            List<String[]> rbac = new ArrayList<>();

            for (String s : scopes) {
                //Require other permissions; Go for rbac system and verifying, and do not return any userInfos.
                String[] action = s.split(":");

                if (action.length != 2) throw new BizException(ErrorType.Showed.Params, "Scope not match");
                if (action[0].equals("info")) infos.add(action);
                else rbac.add(action);
            }

            Set<PlatformType> requires = PlatformType.fromObjs(infos.stream().map(strings -> strings[1]).toList());
            if (!requires.isEmpty()) {
                Set<PlatformType> allows = app.getRequiredUserInfos().stream().map(ThirdPartyRequiredUserInfo::getPlatformType).collect(Collectors.toSet());
                if (!allows.containsAll(requires))
                    throw new BizException(ErrorType.Showed.Params, "Auth error, info required is not allowed");
            }

            if (rbac.isEmpty()) return Future.succeededFuture();
            else if (rbac.size() == 1) {
                return authService.rbac(Action.required(new RbacUserInfo(user.getId(), user.getName()), new TargetInfo(app.getId(), rbac.get(0)[0], rbac.get(0)[1]))).map(v -> {
                    if (!v.success()) throw new BizException(ErrorType.Showed.Params, "Auth error, rbac auth failed");
                    return null;
                });
            } else {
                var actions = rbac.stream().map(r -> {
                    return Action.required(new RbacUserInfo(user.getId(), user.getName()), new TargetInfo(app.getId(), r[0], r[1]));
                }).toList();
                return authService.rbac(actions).map(v -> {
                    if (!v.success()) throw new BizException(ErrorType.Showed.Params, "Auth error, rbac auth failed");
                    return null;
                });
            }
        }
    }

    //verify scope and return userInfo required
    private Future<UserInfo> verifyScopeWithUserInfo(User user, ThirdPartyApp app, String scope) {
        if (scope == null || scope.isBlank()) throw new BizException(ErrorType.Showed.Params, "Scope not exists");

        if (scope.equals("all")) {
            //Require all permissions; Go for rbac system and verifying, and do not return any userInfos.
            return authService.rbac(Action.required(new RbacUserInfo(user.getId(), user.getName()), new TargetInfo(app.getId(), "all", "all"))).map(v -> {
                if (!v.success()) throw new BizException(ErrorType.Showed.Params, "Auth error, rbac auth failed");
                return new UserInfo(user.getName(), user.getId(), new ArrayList<>());
            });
        } else {
            String[] scopes = scope.split(" ");
            List<String[]> infos = new ArrayList<>();
            List<String[]> rbac = new ArrayList<>();

            for (String s : scopes) {
                //Require other permissions; Go for rbac system and verifying, and do not return any userInfos.
                String[] action = s.split(":");
                if (action.length != 2) throw new BizException(ErrorType.Showed.Params, "Scope not match");
                if (action[0].equals("info")) infos.add(action);
                else rbac.add(action);
            }

            var userInfo = new UserInfo(user.getName(), user.getId(), new ArrayList<>());
            Set<PlatformType> requires = PlatformType.fromObjs(infos.stream().map(strings -> strings[1]).toList());

            Future<Void> future;

            if (!requires.isEmpty()) {
                Set<PlatformType> allows = app.getRequiredUserInfos().stream().map(ThirdPartyRequiredUserInfo::getPlatformType).collect(Collectors.toSet());
                if (!allows.containsAll(requires))
                    throw new BizException(ErrorType.Showed.Params, "Auth error, info required is not allowed");
                //Require getting infos and user allowed while authorization; return userInfos that allowed.
                future = userInfoRepository.findByUserIdAndPlatformTypes(user.getId(), requires).onSuccess(userInfos -> {
                    userInfos.forEach(info ->
                            userInfo.additional().add(
                                    new UserInfo.Additional(
                                            info.getPlatformType(),
                                            info.getDataType(),
                                            info.getMetadata()
                                    ))
                    );
                }).mapEmpty();
            } else {
                future = Future.succeededFuture();
            }

            if (rbac.isEmpty()) return future.map(userInfo);
            else if (rbac.size() == 1) {
                return authService.rbac(Action.required(new RbacUserInfo(user.getId(), user.getName()), new TargetInfo(app.getId(), rbac.get(0)[0], rbac.get(0)[1]))).map(v -> {
                    if (!v.success()) throw new BizException(ErrorType.Showed.Params, "Auth error, rbac auth failed");
                    return userInfo;
                });
            } else {
                var actions = rbac.stream().map(r -> {
                    return Action.required(new RbacUserInfo(user.getId(), user.getName()), new TargetInfo(app.getId(), r[0], r[1]));
                }).toList();
                return authService.rbac(actions).map(v -> {
                    if (!v.success()) throw new BizException(ErrorType.Showed.Params, "Auth error, rbac auth failed");
                    return userInfo;
                });
            }

        }

    }

    @Override
    public Future<com.qingyou.sso.api.result.AuthorizationCode> authorization(Long userId, OAuth2Params.Authorization authorization) {
        String code = StringUtils.random(32);
        return thirdPartyRepository.findByClientId(authorization.clientId()).flatMap(app -> {
            if (app == null)
                throw new BizException(ErrorType.OAuth2.INVALID_CLIENT, "App not exists");
            if (app.getRequiredUserInfos() != null && app.getRedirectURIs().stream().noneMatch(redirect -> redirect.getURI().equals(authorization.redirectURI())))
                throw new BizException(ErrorType.OAuth2.INVALID_REDIRECT_URI, "URI not matched");
            AuthCache auth = new AuthCache(userId,authorization.clientId(),authorization.redirectURI(),authorization.codeChallenge(),authorization.scope());
            return cache.set("auth_code:" + code, auth, codeExpireTime);
        }).map(new AuthorizationCode(code));
    }

    record AuthCache(Long user,String client, String redirectURI, String challenge, String scope) {
        public boolean requiredPKCE() {
            return challenge != null;
        }
    }

    @Override
    public Future<com.qingyou.sso.api.result.AccessToken> verify(OAuth2Params.Verify verify) {
        var auth = cache.get("auth_code:" + verify.code(), AuthCache.class).flatMap(result -> {
            if (result == null) throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "Code not exists");
            else return cache.delete("auth_code:" + verify.code()).map(result);
        });

        return auth.flatMap(values -> {
            if (values.requiredPKCE()) {
                if (verify.codeVerifier() == null) throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "Code verifier required");
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    if (!Objects.equals(values.challenge, Base64.getUrlEncoder().withoutPadding().encodeToString(digest.digest(verify.codeVerifier().getBytes(StandardCharsets.UTF_8)))))
                        throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "Challenge not match");
                } catch (NoSuchAlgorithmException e) {
                    throw new BizException(ErrorType.OAuth2.SERVER_ERROR, "Message digest SHA-256 not exists", e);
                }
            }
            return thirdPartyRepository.findByClientId(verify.clientId());
        }).flatMap(app -> {
            if (app == null)
                throw new BizException(ErrorType.OAuth2.UNAUTHORIZED_CLIENT, "App not exists");
            if (!verify.clientSecret().equals(app.getClientSecret())) throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "Client secret not match");
            String scope = auth.result().scope();
            Long userId = auth.result().user();

            return userRepository.findById(userId).flatMap(user -> {
                return verifyScope(user, app, scope);
            }).map(v -> {
                String accessToken = JWT.create().withSubject(userId.toString())
                        .withClaim("client", verify.clientId())
                        .withClaim("scope", scope)
                        .withIssuedAt(Instant.now())
                        .withExpiresAt(Instant.now().plus(accessTokenExpireTime))
                        .sign(algorithm);
                String refreshToken = JWT.create().withSubject(userId.toString())
                        .withClaim("client", verify.clientId())
                        .withClaim("scope", scope)
                        .withClaim("grant_type", "refresh_token")
                        .withIssuedAt(Instant.now())
                        .withExpiresAt(Instant.now().plus(refreshTokenExpireTime))
                        .sign(algorithm);
                return new AccessToken(accessToken, "bearer", refreshToken, accessTokenExpireIn, scope);
            });
        });
    }

    @Override
    public Future<com.qingyou.sso.api.result.AccessToken> implicitAuthorization(Long userId, OAuth2Params.Authorization authorization) {
        return thirdPartyRepository.findByClientId(authorization.clientId()).flatMap(app -> {
            if (app == null)
                throw new BizException(ErrorType.OAuth2.UNAUTHORIZED_CLIENT, "App not exists");
            if (app.getRedirectURIs().stream().noneMatch(redirect -> redirect.getURI().equals(authorization.redirectURI())))
                throw new BizException(ErrorType.OAuth2.INVALID_REDIRECT_URI, "URI not matched");
            return userRepository.findById(userId).flatMap(user -> {
                return verifyScope(user, app, authorization.scope());
            }).map(v -> {
                String accessToken = JWT.create().withSubject(userId.toString())
                        .withClaim("client", authorization.clientId())
                        .withClaim("scope", authorization.scope())
                        .withIssuedAt(Instant.now())
                        .withExpiresAt(Instant.now().plus(accessTokenExpireTime))
                        .sign(algorithm);
                return new AccessToken(accessToken, "bearer", null, accessTokenExpireIn, authorization.scope());
            });
        });
    }

    @Override
    public Future<com.qingyou.sso.api.result.AccessToken> refresh(OAuth2Params.Refresh refresh) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = verifier.verify(refresh.refreshToken());
        } catch (TokenExpiredException e) {
            throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "Refresh token expired", e);
        } catch (JWTVerificationException e) {
            throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "Refresh token not valid", e);
        }

        long userId = Long.parseLong(decodedJWT.getSubject());
        String scope = decodedJWT.getClaim("scope").asString();
        Claim verify = decodedJWT.getClaim("grant_type");
        String client = decodedJWT.getClaim("client").asString();

        if (!client.equals(refresh.clientId()))
            throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "ClientId not match");
        if (verify.isMissing() || verify.isNull() || !verify.asString().equals("refresh_token"))
            throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "Refresh token not match");

        return thirdPartyRepository.findByClientId(refresh.clientId()).flatMap(app -> {
            if (app == null)
                throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "App not exists");
            if (!refresh.clientSecret().equals(app.getClientSecret()))
                throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "Client secret not match");
            return userRepository.findById(userId).flatMap(user -> {
                return verifyScope(user, app, scope);
            }).map(v -> {
                String genAccessToken = JWT.create().withSubject(Long.toString(userId))
                        .withClaim("client", refresh.clientId())
                        .withClaim("scope", scope)
                        .withIssuedAt(Instant.now())
                        .withExpiresAt(Instant.now().plus(accessTokenExpireTime))
                        .sign(algorithm);
                String genRefreshToken = JWT.create().withSubject(Long.toString(userId))
                        .withClaim("client", refresh.clientId())
                        .withClaim("scope", scope)
                        .withClaim("grant_type", "refresh_token")
                        .withIssuedAt(Instant.now())
                        .withExpiresAt(Instant.now().plus(refreshTokenExpireTime))
                        .sign(algorithm);
                return new AccessToken(genAccessToken, "bearer", genRefreshToken, accessTokenExpireIn, scope);
            });
        });
    }

    @Override
    public Future<ResourceData<com.qingyou.sso.api.result.UserInfo>> info(OAuth2Params.Info info) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = verifier.verify(info.accessToken());
        } catch (TokenExpiredException e) {
            throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "Access token expired", e);
        } catch (JWTVerificationException e) {
            throw new BizException(ErrorType.OAuth2.ACCESS_DENIED, "Access Token not valid", e);
        }

        long userId = Long.parseLong(decodedJWT.getSubject());
        String scope = decodedJWT.getClaim("scope").asString();
        String client = decodedJWT.getClaim("client").asString();

        if (!decodedJWT.getClaim("grant_type").isMissing())
            throw new BizException(ErrorType.OAuth2.INVALID_REQUEST, "Access token not match");

        return userRepository.findById(userId).flatMap(user ->
                thirdPartyRepository.findByClientId(client).flatMap(app ->
                        verifyScopeWithUserInfo(user, app, scope)
                )
        ).map(ResourceData::new);
    }

    @Override
    public Future<com.qingyou.sso.api.result.AccessToken> password(OAuth2Params.Password password) {
        return thirdPartyRepository.findByClientId(password.clientId()).flatMap(app -> {
            if (app == null || !Objects.equals(app.getClientSecret(), password.clientSecret()))
                throw new BizException(ErrorType.OAuth2.UNAUTHORIZED_CLIENT, "App not exists");
            return userRepository.findByUsername(password.username()).map(user -> {
                var words = PasswordEncodeUtils.encode(password.password(), user.getAccount().getSalt());
                if (!words.encoded().equals(user.getAccount().getPassword()))
                    throw new BizException(ErrorType.Inner.Login, "Password incorrect");
                return user;
            }).flatMap(user -> {
                return verifyScope(user, app, password.scope()).map(user);
            }).map(user -> {
                String accessToken = JWT.create().withSubject(user.toString())
                        .withClaim("client", password.clientId())
                        .withClaim("scope", password.scope())
                        .withIssuedAt(Instant.now())
                        .withExpiresAt(Instant.now().plus(accessTokenExpireTime))
                        .sign(algorithm);
                return new AccessToken(accessToken, "bearer", null, accessTokenExpireIn, password.scope());
            });
        });
    }
}
