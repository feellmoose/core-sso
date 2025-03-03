package com.qingyou.sso.verticle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyou.sso.infra.Constants;
import com.qingyou.sso.infra.config.ConfigLoader;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.inject.provider.BaseModule;
import com.qingyou.sso.utils.HibernateUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.redis.client.*;
import lombok.Getter;
import org.hibernate.reactive.mutiny.Mutiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class CoreVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(CoreVerticle.class);
    @Getter
    private BaseModule baseModule;

    private Mutiny.SessionFactory sessionFactory;
    private Redis redis;

    @Override
    public void start(Promise<Void> startPromise) {
        var config = new ConfigLoader(vertx).load().andThen(result -> {
            if (result.succeeded()) {
                log.info("Load Conf successfully");
                var application = result.result().application();
                log.info("\n{}", Constants.logo(application.name(), application.version()));
            } else {
                throw new BizException(ErrorType.Inner.Init, "Load Conf failed", result.cause());
            }
        });

        //load redis and test connection
        var redisFuture = config.flatMap(conf -> {
            switch (conf.redis().mode()){
                case Standalone -> {
                    log.info("Loading Standalone Redis");
                    RedisOptions options = new RedisOptions()
                            .setType(RedisClientType.STANDALONE)
                            .setPassword(conf.redis().password())
                            .setConnectionString(conf.redis().url());
                    redis = Redis.createClient(vertx, options);
                }
                case Sentinel -> {
                    log.info("Loading Sentinel Redis");
                    var sentinel = conf.redis().sentinel();
                    if (sentinel == null) throw new BizException(ErrorType.Inner.Init, "Load Sentinel Redis failed, Sentinel is null");
                    RedisOptions options = new RedisOptions()
                            .setUseReplicas(RedisReplicas.ALWAYS)
                            .setType(RedisClientType.SENTINEL)
                            .setAutoFailover(true)
                            .setMasterName(sentinel.master())
                            .setPassword(conf.redis().password());
                    for (var node : sentinel.nodes()) {
                        options.addConnectionString(node);
                    }
                    redis = Redis.createClient(vertx, options);
                }
                default -> {
                    throw new BizException(ErrorType.Inner.Init, "Load Redis failed, Not support mode");
                }
            }
            return redis.connect().onSuccess(RedisConnection::close);
        }).andThen(result -> {
            if (result.succeeded()) {
                log.info("Redis is ready");
            } else {
                throw new BizException(ErrorType.Inner.Init, "Connect to redis failed", result.cause());
            }
        });

        var sessionFactoryFuture = config.flatMap(conf ->
                vertx.executeBlocking(() -> {
                    try {
                        log.info("Loading Hibernate-Reactive");
                        var factory = HibernateUtils.getEntityManagerFactory(conf).unwrap(Mutiny.SessionFactory.class);
                        log.info("Hibernate-Reactive is ready");
                        return factory;
                    } catch (Exception e) {
                        throw new BizException(ErrorType.Inner.Init, "Hibernate Reactive is failed", e);
                    }
                })
        );

        List<Future<?>> prepare = List.of(config, sessionFactoryFuture, redisFuture);
        Future.all(prepare).onSuccess(v -> {
            sessionFactory = sessionFactoryFuture.result();
            baseModule = new BaseModule(config.result(), sessionFactoryFuture.result(), redis, new ObjectMapper(), vertx);
            startPromise.complete();
        }).onFailure(startPromise::fail);
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        var sec = vertx.<Void>executeBlocking(() -> {
            sessionFactory.close();
            return null;
        });
        sec.onSuccess(f -> {
            redis.close();
            log.info("Service stopped");
            stopPromise.complete();
        }).onFailure(stopPromise::fail);
    }

}
