package com.qingyou.sso.infra.response;

import com.qingyou.sso.api.dto.Result;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

@AllArgsConstructor
public class EventMessageHandler<T, R> implements Handler<Message<T>> {

    private final Function<T, Future<R>> dataHandler;

    private static final Logger log = LoggerFactory.getLogger(EventMessageHandler.class);

    @Override
    public void handle(Message<T> message) {
        try {
            Future<R> future;
            try {
                future = dataHandler.apply(message.body());
            } catch (Throwable ex) {
                fail(message, ex);
                return;
            }
            end(message, future);
        } catch (Throwable ex) {
            log.error("Un handle RuntimeException(message={})", ex.getMessage(), ex);
            message.fail(500, ex.getMessage());
        }
    }

    public static <T, R> EventMessageHandler<T, R> wrap(Function<T, Future<R>> dataHandler) {
        return new EventMessageHandler<>(dataHandler);
    }

    public static <T, R> EventMessageHandler<String, R> wrap(Function<String, T> deserializer, Function<T, Future<R>> dataHandler) {
        return new EventMessageHandler<>(json -> {
            T data = deserializer.apply(json);
            return dataHandler.apply(data);
        });
    }

    private void end(Message<T> message, Future<R> data) {
        data.onComplete(result -> {
            if (result.succeeded()) success(message, result.result());
            else fail(message, result.cause());
        });
    }

    private void success(Message<T> message, R data) {
        message.reply(Json.encodePrettily(Result.success(data)));
    }

    private void fail(Message<T> message, Throwable ex) {
        if (ex == null) {
            Result<String> result = Result.failed(ErrorType.Inner.Default.code(), ErrorType.Inner.Default.message());
            message.reply(Json.encodePrettily(result));
            log.error("RuntimeException(message=)");
        } else if (ex instanceof BizException bizException) {
            var error = bizException.getErrorType();
            Result<String> result;
            if (error instanceof ErrorType.Inner) result = Result.failed(error.code(), error.message());
            else result = Result.failed(error.code(), ex.getMessage());
            message.reply(Json.encodePrettily(result));
            log.error("BizException(code={},error={},message={})", error.code(), error.message(), ex.getMessage(), ex);
        } else {
            Result<String> result = Result.failed(ErrorType.Inner.Default.code(), ErrorType.Inner.Default.message());
            message.reply(Json.encodePrettily(result));
            log.error("RuntimeException(message={})", ex.getMessage(), ex);
        }
    }
}
