package com.qingyou.sso.router.sso;

import com.qingyou.sso.handler.platform.CustomSSOHandler;
import com.qingyou.sso.handler.platform.SSOHandlerRegistry;
import com.qingyou.sso.service.BaseSSOService;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class SSORouterHandlerRegistry {

    private final List<CustomSSORouterHandler> customSSORouterHandlers;

    public SSORouterHandlerRegistry(BaseSSOService baseSSOService) {
        this.customSSORouterHandlers = load(baseSSOService);
    }

    public List<CustomSSORouterHandler> getAll() {
        return this.customSSORouterHandlers;
    }

    public List<CustomSSORouterHandler> load(BaseSSOService ssoService) {
        ServiceLoader<SSOHandlerRegistry> serviceLoader = ServiceLoader.load(SSOHandlerRegistry.class);
        return serviceLoader.stream().map(custom ->
                new CustomSSORouterHandler(custom.get().getSSOHandler(ssoService))
        ).collect(Collectors.toList());
    }

    public SSORouterHandlerRegistry register(CustomSSORouterHandler customRouterHandler) {
        customSSORouterHandlers.add(customRouterHandler);
        return this;
    }
}
