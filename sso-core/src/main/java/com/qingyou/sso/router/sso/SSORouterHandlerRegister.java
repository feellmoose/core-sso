package com.qingyou.sso.router.sso;

import com.qingyou.sso.handler.platform.CustomRegisterHandler;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class SSORouterHandlerRegister {

    public static final SSORouterHandlerRegister Instance = new SSORouterHandlerRegister();

    private final List<CustomRegisterRouterHandler> customRegisterRouterHandlers;

    private SSORouterHandlerRegister() {
        this.customRegisterRouterHandlers = load();
    }

    public List<CustomRegisterRouterHandler> getAll() {
        return this.customRegisterRouterHandlers;
    }

    public List<CustomRegisterRouterHandler> load() {
        ServiceLoader<CustomRegisterHandler> serviceLoader = ServiceLoader.load(CustomRegisterHandler.class);
        return serviceLoader.stream().map(custom ->
                new CustomRegisterRouterHandler(custom.get())
        ).collect(Collectors.toList());
    }

    public SSORouterHandlerRegister register(CustomRegisterRouterHandler customRouterHandler) {
        customRegisterRouterHandlers.add(customRouterHandler);
        return this;
    }
}
