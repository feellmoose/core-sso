package com.qingyou.sso.router.sso;

import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SSORouterHandlerRegister {

    public static final SSORouterHandlerRegister Instance = new SSORouterHandlerRegister();

    private final List<CustomRegisterRouterHandler> customRegisterRouterHandlers;

    private SSORouterHandlerRegister() {
        this.customRegisterRouterHandlers = new ArrayList<>();
    }

    public List<CustomRegisterRouterHandler> getAll() {
        Set<String> names = this.customRegisterRouterHandlers.stream()
                .map(CustomRegisterRouterHandler::getName)
                .collect(Collectors.toSet());
        if (names.size() != this.customRegisterRouterHandlers.size())
            throw new BizException(ErrorType.Inner.Init,"Custom_register_login_handler name can't be duplicated");
        return this.customRegisterRouterHandlers;
    }

    public SSORouterHandlerRegister register(CustomRegisterRouterHandler customRouterHandler) {
        customRegisterRouterHandlers.add(customRouterHandler);
        return this;
    }
}
