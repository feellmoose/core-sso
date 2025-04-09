package com.qingyou.sso.verticle;

import com.qingyou.sso.auth.api.dto.Action;
import com.qingyou.sso.auth.internal.rbac.Rbac;
import com.qingyou.sso.infra.Constants;
import com.qingyou.sso.infra.cache.DefaultCache;
import com.qingyou.sso.infra.config.ConfigurationSourceLoader;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.infra.response.EventMessageHandler;
import com.qingyou.sso.inject.DaggerRouterHandlerRegisterComponent;
import com.qingyou.sso.inject.provider.BaseModule;
import com.qingyou.sso.inject.provider.RouterHandlerModule;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CoreVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(CoreVerticle.class);
    private HttpServer server;
    private SqlClient client;

    @Override
    public void start(Promise<Void> startPromise) {
        var config = new ConfigurationSourceLoader(vertx).loadSource().andThen(result -> {
            if (result.succeeded()) {
                log.info("Load Conf successfully");
                log.info("\n{}", Constants.logo("pomelo-sso", "v1.0.3"));
            } else {
                throw new BizException(ErrorType.Inner.Init, "Load Conf failed", result.cause());
            }
        });

        //load redis and test connection
        var cache = DefaultCache.build(vertx);
        var clientFuture = config.map(c -> connectSqlClient(c.getSource().getJsonObject("database"),vertx));

        Future.all(List.of(config, clientFuture, cache))
                .map(v -> {
                    client = clientFuture.result();
                    return new BaseModule(config.result(), vertx, client, cache.result());
                })
                .map(this::runAuthEventListener)
                .flatMap(this::runHttpServer)
                .onSuccess(result -> startPromise.complete())
                .onFailure(startPromise::fail);

    }

    private SqlClient connectSqlClient(JsonObject json, Vertx vertx) {
        PgConnectOptions connectOptions = new PgConnectOptions(json);

        PoolOptions poolOptions;
        if (json.containsKey("pool")) {
            poolOptions = new PoolOptions(json.getJsonObject("pool"));
        } else {
            poolOptions = new PoolOptions();
        }

        return PgBuilder
                .client()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .using(vertx)
                .build();
    }

    private BaseModule runAuthEventListener(BaseModule baseModule) {
        final Rbac rbac = new Rbac(baseModule.getSqlClient());
        vertx.eventBus().consumer("auth_rbac", EventMessageHandler.wrap(
                json -> Json.decodeValue(json, Action.class)
                , rbac::enforceAndThrows));
        vertx.eventBus().consumer("multi_auth_rbac", EventMessageHandler.<Collection<Action>,CompositeFuture>wrap(json -> {
            JsonArray array = Json.decodeValue(json, JsonArray.class);
            List<Action> actions = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                actions.add(Json.decodeValue(array.getString(i), Action.class));
            }
            return actions;
        }, actions -> {
            List<Future<Void>> futures = new ArrayList<>();
            for (Action action : actions) {
                futures.add(rbac.enforceAndThrows(action));
            }
            return Future.all(futures);
        }));
        log.info("auth_rbac event handler started");
        return baseModule;
    }

    private Future<HttpServer> runHttpServer(BaseModule baseModule){
        //server start must after the config and the persistence
        var serverConf = baseModule.configuration().getConfiguration().server();

        Router router = Router.router(vertx);
        server = vertx.createHttpServer();

        var register = DaggerRouterHandlerRegisterComponent.builder()
                .baseModule(baseModule)
                .routerHandlerModule(new RouterHandlerModule(router))
                .build();
        register.registerAllGroups();
        register.registerNotFoundRouter();

        if (baseModule.configuration().getConfiguration().mail() != null) {
            register.registerEmailSSORouters();
        }

        log.info("Dagger inject RouterGroups");

        return server.requestHandler(router).listen(serverConf.port(), serverConf.host()).onComplete(http -> {
            if (http.succeeded()) {
                log.info("Http Server started on {}:{}", serverConf.host(), serverConf.port());
            } else {
                throw new BizException(ErrorType.Inner.Init, http.cause());
            }
        }).timeout(5, TimeUnit.SECONDS);
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        server.close().onSuccess(f -> {
            log.info("Http server stopped");
        }).flatMap(v -> {
            return client.close();
        }).flatMap(v -> {
            log.info("Service stopped");
            stopPromise.complete();
            return null;
        }).onFailure(stopPromise::fail);
    }

}
