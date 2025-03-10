package com.qingyou.sso.domain.user;

import com.qingyou.sso.api.constants.DataType;
import com.qingyou.sso.api.constants.PlatformType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserInfo {

    private Long id;

    private String metadata;

    private DataType dataType;

    private PlatformType platformType;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;
}
