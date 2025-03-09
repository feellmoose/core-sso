package com.qingyou.sso.domain.auth;

import lombok.Data;

import java.util.List;

@Data
public class TargetRole {

    private Long id;

    private Long appid;

    private String action;

    private String object;

    private List<Role> roles;

}
