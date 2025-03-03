package com.qingyou.sso.infra.response;

import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface OAuth2HttpResponse {

    String CONTENT_TYPE_HEADER = "Content-Type";
    String APPLICATION_JSON = "application/json";
    Logger log = LoggerFactory.getLogger(OAuth2HttpResponse.class);

    static void fail(RoutingContext routingContext, Throwable ex) {
        fail(routingContext, ex, log);
    }

    static void fail(RoutingContext routingContext, Throwable ex, Logger log) {
        if (ex == null) {
            log.error("RuntimeException(message=)");
            fail(routingContext, ErrorType.OAuth2.SERVER_ERROR);
        } else if (ex instanceof BizException bizException) {
            ErrorType error = bizException.getErrorType();
            log.error("BizException(code={},error={},message={})", error.code(), error.message(), ex.getMessage(), ex);
            if (error instanceof ErrorType.OAuth2) fail(routingContext, (ErrorType.OAuth2) error);
            else fail(routingContext, ErrorType.OAuth2.SERVER_ERROR);
        } else {
            log.error("RuntimeException(message={})", ex.getMessage(), ex);
            fail(routingContext, ErrorType.OAuth2.SERVER_ERROR);
        }
    }

    static void fail(RoutingContext routingContext, ErrorType.OAuth2 errorType) {
        fail(routingContext, errorType, log);
    }

    static void fail(RoutingContext routingContext, ErrorType.OAuth2 errorType, Logger log) {
        log.error("Invalid Oauth 2.0 request, error={}, description={}", errorType.error(), errorType.description());
        routingContext.response()
                .setStatusCode(errorType.code())
                .end(Json.encodeToBuffer(new JsonObject()
                        .put("error", errorType.error())
                        .put("error_description", errorType.description()))
                );
    }

    static <T> void success(RoutingContext routingContext, T data) {
        routingContext.response().setStatusCode(200)
                .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                .end(Json.encodeToBuffer(data));
    }

    static void failRedirect(RoutingContext routingContext, String url, Throwable ex) {
        failRedirect(routingContext, url, ex, log);
    }

    static void failRedirect(RoutingContext routingContext, String url, Throwable ex, Logger log) {
        if (ex instanceof BizException bizException) {
            ErrorType errorType = bizException.getErrorType();
            log.error("BizException(code={},error={},message={})", errorType.code(), errorType.message(), ex.getMessage(), ex);
            if (errorType instanceof ErrorType.OAuth2 error) {
                failRedirect(routingContext, url, error, log);
                return;
            }
        } else {
            log.error("RuntimeException(message={})", ex.getMessage(), ex);
        }
        ErrorType.OAuth2 error = ErrorType.OAuth2.SERVER_ERROR;
        failRedirect(routingContext, url, error, log);
    }

    static void failRedirect(RoutingContext routingContext, String url, ErrorType.OAuth2 errorType) {
        failRedirect(routingContext, url, errorType, log);
    }

    static void failRedirect(RoutingContext routingContext, String url, ErrorType.OAuth2 errorType, Logger log) {
        log.error("Invalid Oauth 2.0 request redirect to url={}, error={}, description={}", url, errorType.error(), errorType.description());
        routingContext.redirect(url +"?error=" +errorType.error() +"&error_description=" + errorType.description());
    }

    static void redirect(RoutingContext routingContext, String url) {
        routingContext.redirect(url);
    }

}
