package com.qingyou.sso.domain.oauth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class ThirdPartyRedirect {

    private Long id;

    private String URI;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ThirdPartyApp thirdPartyApp;
}