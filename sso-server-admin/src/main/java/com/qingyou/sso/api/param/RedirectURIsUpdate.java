package com.qingyou.sso.api.param;

import java.util.List;

public record RedirectURIsUpdate(
        String clientId,
        List<String> redirectURIs
) {
}
