package com.qingyou.sso.domain.user;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;

    private String name;

    private String email;

    private String phone;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude

    private Account account;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserInfo> userInfo;
}
