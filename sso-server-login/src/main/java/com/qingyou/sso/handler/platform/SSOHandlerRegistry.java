package com.qingyou.sso.handler.platform;

import com.qingyou.sso.service.BaseSSOService;

public interface SSOHandlerRegistry {
   CustomSSOHandler getSSOHandler(BaseSSOService ssoService);
}
