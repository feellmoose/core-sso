package com.qingyou.sso.api.result;

import io.vertx.codegen.annotations.Nullable;

public record AccessToken(
        String access_token,
        String token_type,
        @Nullable String refresh_token,
        long expires_in,
        String scope
) {
}
