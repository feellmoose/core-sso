package com.qingyou.sso;

import com.qingyou.sso.verticle.CoreVerticle;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CoreSSOApp {

    private static final Logger log = LoggerFactory.getLogger(CoreSSOApp.class);

    static {
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
    }

    public static void main(String[] args) {
        new CoreSSOApp().start()
                .toCompletionStage()
                .toCompletableFuture()
                .join();
    }

    public Future<Void> start(){
        long start = System.currentTimeMillis();

//        ClusterManager clusterManager = new HazelcastClusterManager();
//
//        var vertx = Vertx.builder()
//                .withClusterManager(clusterManager)
//                .buildClustered();
        var vertx = Future.succeededFuture(Vertx.vertx());

        return vertx.flatMap(v -> {
            var core = new CoreVerticle();
            return v.deployVerticle(core).timeout(10, TimeUnit.SECONDS);
        }).onComplete(result -> {
            if (result.succeeded()) {
                log.info("CoreSSOApp start in {}ms", System.currentTimeMillis() - start);
            } else {
                log.error("CoreSSOApp start failed", result.cause());
            }
        }).mapEmpty();

    }

}
