package com.qingyou.sso.infra.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyou.sso.infra.exception.BizException;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;

import java.io.IOException;;

@AllArgsConstructor
public class HttpRequestBody {
    private final ObjectMapper objectMapper;

    public <T> Future<T> json(RoutingContext routingContext, Class<T> clazz) {
        return routingContext.request().body().map(buffer -> {
            try {
                return objectMapper.readValue(buffer.getBytes(), clazz);
            } catch (IOException e) {
                throw new BizException(e);
            }
        });
    }
}
