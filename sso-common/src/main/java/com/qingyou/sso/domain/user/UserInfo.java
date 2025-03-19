package com.qingyou.sso.domain.user;

import com.qingyou.sso.api.constants.DataType;
import com.qingyou.sso.api.constants.PlatformType;
import com.qingyou.sso.api.dto.ThirdPartySSOUserInfo;
import lombok.*;

import java.util.Map;

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

    public Map<String,String> getValue(){
        return dataType.decode(metadata);
    }
}
