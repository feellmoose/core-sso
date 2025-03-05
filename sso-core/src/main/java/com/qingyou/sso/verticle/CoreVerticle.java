package com.qingyou.sso.verticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyou.sso.auth.api.dto.Action;
import com.qingyou.sso.auth.internal.rbac.Rbac;
import com.qingyou.sso.auth.internal.rbac.RbacUserInfo;
import com.qingyou.sso.auth.internal.rbac.TargetInfo;
import com.qingyou.sso.infra.Constants;
import com.qingyou.sso.infra.cache.DefaultCache;
import com.qingyou.sso.infra.config.ConfigLoader;
import com.qingyou.sso.infra.config.Configuration;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.infra.response.EventMessageHandler;
import com.qingyou.sso.inject.DaggerRouterHandlerRegisterComponent;
import com.qingyou.sso.inject.provider.BaseModule;
import com.qingyou.sso.inject.provider.RouterHandlerModule;
import com.qingyou.sso.utils.HibernateUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.hibernate.reactive.mutiny.Mutiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CoreVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(CoreVerticle.class);
    private HttpServer server;

    private Mutiny.SessionFactory sessionFactory;

    @Override
    public void start(Promise<Void> startPromise) {
        var config = new ConfigLoader(vertx).load().andThen(result -> {
            if (result.succeeded()) {
                log.info("Load Conf successfully");
                log.info("\n{}", Constants.logo("pomelo-sso", "v1.0.1"));
            } else {
                throw new BizException(ErrorType.Inner.Init, "Load Conf failed", result.cause());
            }
        });

        //load redis and test connection
        var sessionFactoryFuture = config.flatMap(this::connectSessionFactory);
        var cache = DefaultCache.build(vertx);

        Future.all(List.of(config, sessionFactoryFuture, cache))
                .map(v -> {
                    sessionFactory = sessionFactoryFuture.result();
                    return new BaseModule(config.result(), sessionFactoryFuture.result(), new ObjectMapper(), vertx, cache.result());
                })
                .map(this::runAuthHandler)
                .flatMap(this::runHttpServer)
                .onSuccess(result -> startPromise.complete())
                .onFailure(startPromise::fail);

    }

    private Future<Mutiny.SessionFactory> connectSessionFactory(Configuration config) {
        return vertx.executeBlocking(() -> {
            try {
                log.info("Loading Hibernate-Reactive");
                var factory = HibernateUtils.getEntityManagerFactory(config).unwrap(Mutiny.SessionFactory.class);
                log.info("Hibernate-Reactive is ready");
                return factory;
            } catch (Exception e) {
                throw new BizException(ErrorType.Inner.Init, "Hibernate Reactive is failed", e);
            }
        });
    }

    private BaseModule runAuthHandler(BaseModule baseModule) {
        final Rbac rbac = new Rbac(baseModule.getSessionFactory());
        var objectMapper = baseModule.getObjectMapper();
        vertx.eventBus().consumer("auth_rbac", EventMessageHandler.wrap(json -> {
            try {
                return objectMapper.<Action<RbacUserInfo, TargetInfo>>readValue(json, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new BizException(e);
            }
        }, action -> rbac.enforceAndThrows(action)));
        vertx.eventBus().consumer("multi_auth_rbac", EventMessageHandler.<Collection<Action<RbacUserInfo, TargetInfo>>,CompositeFuture>wrap(json -> {
            try {
                return objectMapper.readValue(json, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new BizException(e);
            }
        }, actions -> {
            List<Future<Void>> futures = new ArrayList<>();
            for (Action<RbacUserInfo,TargetInfo> action : actions) {
                futures.add(rbac.enforceAndThrows(action));
            }
            return Future.all(futures);
        }));
        log.info("auth_rbac event handler started");
        return baseModule;
    }

    private Future<HttpServer> runHttpServer(BaseModule baseModule){
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

        if (baseModule.configuration().mail() != null) {
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
            return vertx.<Void>executeBlocking(() -> {
                sessionFactory.close();
                log.info("Service stopped");
                stopPromise.complete();
                return null;
            });
        }).onFailure(stopPromise::fail);
    }

}
