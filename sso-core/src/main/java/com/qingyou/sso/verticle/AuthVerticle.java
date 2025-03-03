package com.qingyou.sso.verticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.qingyou.sso.auth.api.dto.Action;
import com.qingyou.sso.api.dto.BaseDependency;
import com.qingyou.sso.auth.internal.rbac.Rbac;
import com.qingyou.sso.auth.internal.rbac.RbacUserInfo;
import com.qingyou.sso.auth.internal.rbac.TargetInfo;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.response.EventMessageHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class AuthVerticle extends AbstractVerticle {
    private final BaseDependency baseDependency;
    private static final Logger log = LoggerFactory.getLogger(AuthVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        final Rbac rbac = new Rbac(baseDependency.getSessionFactory());
        var objectMapper = baseDependency.getObjectMapper();
        vertx.eventBus().consumer("auth_rbac", EventMessageHandler.wrap(json -> {
            try {
                return objectMapper.<Action<RbacUserInfo, TargetInfo>>readValue(json, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new BizException(e);
            }
        }, action -> rbac.enforceAndThrows(action)));
        vertx.eventBus().consumer("multi_auth_rbac", EventMessageHandler.wrap(json -> {
            try {
                return objectMapper.<Collection<Action<RbacUserInfo, TargetInfo>>>readValue(json, new TypeReference<>() {
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
        startPromise.complete();
    }
}
