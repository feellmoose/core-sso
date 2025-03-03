package com.qingyou.sso.auth.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyou.sso.api.dto.Result;
import com.qingyou.sso.auth.api.AuthService;
import com.qingyou.sso.auth.internal.rbac.RbacUserInfo;
import com.qingyou.sso.auth.internal.rbac.TargetInfo;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
public class AuthServiceWithEventBus implements AuthService {
    private final EventBus eventBus;
    private final ObjectMapper objectMapper;

    @Override
    public Future<Result<Void>> rbac(Action<RbacUserInfo, TargetInfo> action) {
        try {
            return eventBus.request("auth_rbac", objectMapper.writeValueAsString(action)).map(objectMessage ->
                (Result<Void>) Json.decodeValue((String) objectMessage.body(),Result.class)
            );
        } catch (Exception e) {
            return Future.failedFuture(new RuntimeException(e));
        }
    }

    @Override
    public Future<Result<Void>> rbac(Collection<Action<RbacUserInfo, TargetInfo>> actions) {
        try {
            return eventBus.request("multi_auth_rbac", objectMapper.writeValueAsString(actions)).map(objectMessage ->
                    (Result<Void>) Json.decodeValue((String) objectMessage.body(),Result.class)
            );
        } catch (Exception e) {
            return Future.failedFuture(new RuntimeException(e));
        }
    }
}
