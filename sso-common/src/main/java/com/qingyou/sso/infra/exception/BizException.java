package com.qingyou.sso.infra.exception;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final ErrorType errorType;

    public BizException(String message) {
        super(message);
        this.errorType = ErrorType.Inner.Default;
    }
    public BizException(Throwable cause) {
        super(cause);
        this.errorType = ErrorType.Inner.Default;
    }
    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.errorType = ErrorType.Inner.Default;
    }

    public BizException(ErrorType errorType,String message) {
        super(message);
        this.errorType = errorType;
    }
    public BizException(ErrorType errorType,String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }
    public BizException(ErrorType errorType,Throwable cause) {
        super(cause);
        this.errorType = errorType;
    }

}
