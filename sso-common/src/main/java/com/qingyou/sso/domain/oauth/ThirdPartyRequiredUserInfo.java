package com.qingyou.sso.domain.oauth;

import com.qingyou.sso.api.constants.DataType;
import com.qingyou.sso.api.constants.PlatformType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class ThirdPartyRequiredUserInfo {

    private Long id;
    private DataType dataType;
    private PlatformType platformType;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ThirdPartyApp thirdPartyApp;
}