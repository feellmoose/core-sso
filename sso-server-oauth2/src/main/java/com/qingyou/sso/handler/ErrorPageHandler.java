package com.qingyou.sso.handler;

import com.qingyou.sso.api.Error;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.infra.response.GlobalHttpResponse;
import com.qingyou.sso.infra.response.OAuth2HttpResponse;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorPageHandler implements Error {

    @Override
    public void error(RoutingContext routingContext) {
        String error = routingContext.queryParams().get("error");
        String description = routingContext.queryParams().get("error_description");
        ErrorType.OAuth2 errorType = ErrorType.OAuth2.of(error,description);
        if (errorType != null) {
            OAuth2HttpResponse.fail(routingContext, errorType, log);
        }else {
            GlobalHttpResponse.fail(routingContext, new BizException("Error redirect page[error=" + error +", description="+ description +"]"), log);
        }
    }
}
