package com.qingyou.sso.api.result;


import com.qingyou.sso.api.constants.DataType;
import com.qingyou.sso.api.constants.PlatformType;

import java.util.List;

public record UserInfo(
        String name,
        Long id,
        List<Additional> additional
) {
    public record Additional(
            PlatformType platform,
            DataType dataType,
            String metadata
    ) {
    }
}
