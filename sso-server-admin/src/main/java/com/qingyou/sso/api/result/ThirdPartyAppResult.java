package com.qingyou.sso.api.result;

import com.qingyou.sso.api.constants.PlatformType;

import java.util.List;

public record ThirdPartyAppResult(
        String name,
        String clientId,
        String clientSecret,
        List<PlatformType> requiredInfos,
        List<String> redirectURIs
){
}
