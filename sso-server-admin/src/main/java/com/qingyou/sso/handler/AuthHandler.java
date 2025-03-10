package com.qingyou.sso.handler;

import com.qingyou.sso.auth.api.AuthService;
import com.qingyou.sso.auth.api.dto.Action;
import com.qingyou.sso.auth.internal.rbac.RbacUserInfo;
import com.qingyou.sso.auth.internal.rbac.TargetInfo;
import com.qingyou.sso.domain.user.User;
import com.qingyou.sso.infra.exception.BizException;
import com.qingyou.sso.infra.exception.ErrorType;
import com.qingyou.sso.infra.repository.domain.UserRepository;
import com.qingyou.sso.infra.response.GlobalHttpResponse;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthHandler implements Handler<RoutingContext> {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final String action;
    private final String object;

    @Override
    public void handle(RoutingContext routingContext) {
        Session session = routingContext.session();
        if (session == null) {
            GlobalHttpResponse.fail(routingContext, new BizException(ErrorType.Showed.Auth, "Session not exist"), log);
            return;
        }
        Long userId = session.<Long>get("userId");
        if (userId == null) {
            GlobalHttpResponse.fail(routingContext, new BizException(ErrorType.Showed.Auth, "Login required"), log);
            return;
        }
        userRepository.findById(userId)
            .flatMap(user -> {
                if (user == null) throw new BizException(ErrorType.Showed.Auth,"User not exist");
                return authService.rbac(Action.required(new RbacUserInfo(user.getId(), user.getName()), new TargetInfo(0L,action,object))).map(result -> {
                    if (!result.success()) throw new BizException(ErrorType.Showed.Auth, "Auth failed, " + result.message());
                    return user;
                });
            })
            .onSuccess(user -> routingContext.put("user", user).next())
            .onFailure(ex -> GlobalHttpResponse.fail(routingContext, ex, log));
    }

    public record Factory(
            UserRepository userRepository,
            AuthService authService
    ){
        public TargetSpec target(String action, String object) {
            return new TargetSpec(this, action, object);
        }

        public record TargetSpec(
                Factory factory,
                String action,
                String object
        ){
             public AuthHandler build() {
                 return new AuthHandler(factory.userRepository, factory.authService, action,object);
             }
        }
    }

    private AuthHandler(UserRepository userRepository, AuthService authService, String action, String object) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.action = action;
        this.object = object;
    }

}
