package com.qingyou.sso.test;

import com.qingyou.sso.verticle.CoreVerticle;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(VertxExtension.class)
class ApiTest {

    static final CoreVerticle coreVerticle = new CoreVerticle();

    @BeforeAll
    static void deployVerticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(coreVerticle, testContext.succeedingThenComplete());
    }

    @Test
    public void test(Vertx vertx, VertxTestContext testContext){
    }

}
