package com.qingyou.sso;

import com.qingyou.sso.inject.provider.BaseModule;
import com.qingyou.sso.verticle.AuthVerticle;
import com.qingyou.sso.verticle.CoreVerticle;
import com.qingyou.sso.verticle.HttpVerticle;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    static {
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        var vertx = Vertx.vertx();
        var module = Application.startCore(vertx);

        var httpFuture = module.flatMap(baseModule -> vertx.deployVerticle(new HttpVerticle(baseModule)));
        var authFuture = module.flatMap(baseModule -> vertx.deployVerticle(new AuthVerticle(baseModule)));

        var list = List.of(module, httpFuture, authFuture);

        Future.all(list).timeout(10, TimeUnit.SECONDS).onComplete(result -> {
            if (result.succeeded()) {
                log.info("Application start in {}ms", System.currentTimeMillis() - start);
            } else {
                log.error("Application start failed", result.cause());
                System.exit(-1);
            }
        });
    }

    public static Future<BaseModule> startCore(Vertx vertx) {
        var core = new CoreVerticle();
        var coreFuture = vertx.deployVerticle(core);
        return coreFuture.map(v -> core.getBaseModule());
    }

}
