package com.qingyou.sso.test;

import com.qingyou.sso.verticle.CoreVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;

@Slf4j
@ExtendWith(VertxExtension.class)
class SystemTest {


    static final String clientId = "1";
    static final String clientSecret = "1";
    static final String username = "test";
    static final String password = "test";
    static OAuth2Auth oauth2;

    @BeforeAll
    static void deployVerticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new CoreVerticle(), testContext.succeedingThenComplete());
        oauth2 = OAuth2Auth.create(vertx, new OAuth2Options()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setSite("http://localhost:8080")
                .setAuthorizationPath("/oauth/authorize")
                .setTokenPath("/oauth/token")
                .setUserInfoPath("/oauth/info")
        );
    }

    @BeforeEach
    void startThirdPartyServer(Vertx vertx, VertxTestContext testContext) {
        //client BE
        Router router = Router.router(vertx);
        router.get("/callback")
                .handler(routingContext -> {
                    log.info("Handle callback request {}", routingContext.request().uri());
                    var params = routingContext.queryParams();
                    oauth2.authenticate(
                            new Oauth2Credentials()
                                    .setFlow(OAuth2FlowType.AUTH_CODE)
                                    .setRedirectUri("http://localhost:18080/callback")
                                    .setCodeVerifier(params.get("code_verifier"))
                                    .setCode(params.get("code"))
                                    .setScopes(Collections.singletonList(params.get("scope")))
                    ).onSuccess(user -> {
                        routingContext.response().end(user.attributes().toBuffer());
                    }).onFailure(ex -> log.error("BE Server Request error:", ex));
                });
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(18080,"localhost")
                .onSuccess(httpServer -> log.info("Server Started Success on {}",httpServer.actualPort()))
                .onComplete(testContext.succeedingThenComplete());
    }

    @Test
    void oauth2(Vertx vertx, VertxTestContext testContext) {

        //client FE
        WebClient webClient = WebClient.create(vertx);
        //login
        webClient.get(8080, "localhost", "/sso/login")
                .basicAuthentication(username, password)
                .send()
                .map(res -> {
                    log.info("Login response: {}", res.bodyAsString());
                    return res.cookies();
                }).flatMap(cookies -> {
                    //auth
                    String authorization_uri = oauth2.authorizeURL(
                            new OAuth2AuthorizationURL()
                                    .setRedirectUri("http://localhost:18080/callback")
                                    .setScopes(Collections.singletonList("add:app update:app"))
                                    .setState("xyz")
                    ).replace("http://localhost:8080","");
                    log.info("Authorization URI: {}", authorization_uri);
                    return webClient.get(8080,"localhost", authorization_uri)
                            .putHeader("Cookie",cookies)
                            .followRedirects(true)
                            .send()
                            .map(HttpResponse::bodyAsString);
                }).onSuccess(res-> log.info("Auth Success response: {}", res))
                .onComplete(testContext.succeedingThenComplete());
    }

}
