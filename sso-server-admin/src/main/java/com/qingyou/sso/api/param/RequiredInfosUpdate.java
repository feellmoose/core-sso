package com.qingyou.sso.api.param;

import com.qingyou.sso.api.constants.PlatformType;

import java.util.List;

public record RequiredInfosUpdate(
        String clientId,
        List<PlatformType> platformTypes
){}
