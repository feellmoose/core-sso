package com.qingyou.sso.router.sso;

import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomRouterHandlerRegister {

    public static final CustomRouterHandlerRegister Instance = new CustomRouterHandlerRegister();

    private final List<CustomRouterHandler> customRouterHandlers;

    private CustomRouterHandlerRegister() {
        this.customRouterHandlers = new ArrayList<>();
    }

    public List<CustomRouterHandler> getAll() {
        Set<String> names = this.customRouterHandlers.stream()
                .map(CustomRouterHandler::getName)
                .collect(Collectors.toSet());
        if (names.size() != this.customRouterHandlers.size())
            throw new BizException(ErrorType.Inner.Init,"Custom_register_login_handler name can't be duplicated");
        return this.customRouterHandlers;
    }

    public CustomRouterHandlerRegister register(CustomRouterHandler customRouterHandler) {
        customRouterHandlers.add(customRouterHandler);
        return this;
    }
}
