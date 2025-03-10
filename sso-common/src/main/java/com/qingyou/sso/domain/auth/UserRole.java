package com.qingyou.sso.domain.auth;

import lombok.Data;

@Data
public class UserRole {
    private Long id;
    private Long appid;
    private Long userId;
    private Role role;
}
