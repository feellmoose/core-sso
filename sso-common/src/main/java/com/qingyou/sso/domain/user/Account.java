package com.qingyou.sso.domain.user;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    private Long userId;

    private String username;

    private String password;

    private String salt;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;
}
