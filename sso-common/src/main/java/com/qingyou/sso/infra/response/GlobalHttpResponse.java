package com.qingyou.sso.infra.response;

import com.qingyou.sso.api.dto.Result;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface GlobalHttpResponse {

    String CONTENT_TYPE_HEADER = "Content-Type";
    String APPLICATION_JSON = "application/json";
    Logger log = LoggerFactory.getLogger(GlobalHttpResponse.class);

    static <T> void end(RoutingContext routingContext, Future<T> data, Logger log) {
        data.onComplete(result -> {
            if (result.succeeded()) success(routingContext, result.result());
            else fail(routingContext, result.cause(), log);
        });
    }

    static <T> void end(RoutingContext routingContext, Future<T> data) {
        end(routingContext, data, log);
    }

    static <T> void success(RoutingContext routingContext, T data) {
        routingContext.response().setStatusCode(200)
                .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                .end(Json.encodeToBuffer(Result.success(data)));
    }

    static void fail(RoutingContext routingContext, Throwable ex) {
        fail(routingContext, ex, log);
    }

    static void fail(RoutingContext routingContext, Throwable ex, Logger log) {
        if (ex == null) {
            Result<String> result = Result.failed(ErrorType.Inner.Default.code(), ErrorType.Inner.Default.message());
            routingContext.response().setStatusCode(500)
                    .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                    .end(Json.encodeToBuffer(result));
            log.error("RuntimeException(message=)");
        } else if (ex instanceof BizException bizException) {
            var error = bizException.getErrorType();
            Result<String> result;
            if (error instanceof ErrorType.Inner) result = Result.failed(error.code(), error.message());
            else result = Result.failed(error.code(), ex.getMessage());
            routingContext.response().setStatusCode(200)
                    .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                    .end(Json.encodeToBuffer(result));
            log.error("BizException(code={},error={},message={})", error.code(), error.message(), ex.getMessage(), ex);
        } else {
            Result<String> result = Result.failed(ErrorType.Inner.Default.code(), ErrorType.Inner.Default.message());
            routingContext.response().setStatusCode(500)
                    .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                    .end(Json.encodeToBuffer(result));
            log.error("RuntimeException(message={})", ex.getMessage(), ex);
        }
    }

    static <T> GlobalHttpResponseWrap<T> wrap(ServiceApi<T> serviceApi) {
        return new GlobalHttpResponseWrap<>(serviceApi, log);
    }

    static <T> GlobalHttpResponseWrap<T> wrap(ServiceApi<T> serviceApi, Logger log) {
        return new GlobalHttpResponseWrap<>(serviceApi, log);
    }

}
