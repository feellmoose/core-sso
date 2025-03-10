package com.qingyou.sso.domain.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
public class Role {
    private Long id;
    private String name;
    private String description;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserRole> userRole;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<TargetRole> targetRole;

}
