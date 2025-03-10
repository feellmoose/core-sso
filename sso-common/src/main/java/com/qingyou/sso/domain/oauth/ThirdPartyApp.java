package com.qingyou.sso.domain.oauth;

import lombok.Data;

import java.util.List;

@Data
public class ThirdPartyApp {

    private Long id;

    private String appName;

    private String clientId;

    private String clientSecret;

    private List<ThirdPartyRedirect> redirectURIs;

    private List<ThirdPartyRequiredUserInfo> requiredUserInfos;
}
