package com.qingyou.sso.api.param;

import io.vertx.codegen.annotations.Nullable;

public record Code(String email, String code, @Nullable String username,@Nullable String password) {
}
