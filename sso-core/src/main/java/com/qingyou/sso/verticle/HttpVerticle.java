package com.qingyou.sso.verticle;

import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;

import com.qingyou.sso.inject.DaggerRouterHandlerRegisterComponent;
import com.qingyou.sso.inject.provider.BaseModule;
import com.qingyou.sso.inject.provider.RouterHandlerModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class HttpVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(HttpVerticle.class);
    private final BaseModule baseModule;

    public HttpVerticle(BaseModule baseModule) {
        this.baseModule = baseModule;
    }

    private HttpServer server;

    @Override
    public void start(Promise<Void> startPromise) {
        //server start must after the config and the persistence
        var serverConf = baseModule.configuration().server();

        Router router = Router.router(vertx);
        server = vertx.createHttpServer();

        var register = DaggerRouterHandlerRegisterComponent.builder()
                .baseModule(baseModule)
                .routerHandlerModule(new RouterHandlerModule(router))
                .build();
        register.registerAllGroups();
        register.registerNotFoundRouter();
        log.info("Dagger inject RouterGroups");

        server.requestHandler(router).listen(serverConf.port(), serverConf.host()).onComplete(http -> {
            if (http.succeeded()) {
                log.info("Http Server started on {}:{}", serverConf.host(), serverConf.port());
                startPromise.complete();
            } else {
                throw new BizException(ErrorType.Inner.Init, http.cause());
            }
        }).timeout(5, TimeUnit.SECONDS).onFailure(startPromise::fail);
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        server.close().onSuccess(f -> {
            log.info("Http server stopped");
            stopPromise.complete();
        }).onFailure(stopPromise::fail);
    }

}
