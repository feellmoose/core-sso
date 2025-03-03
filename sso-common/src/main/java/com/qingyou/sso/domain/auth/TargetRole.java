package com.qingyou.sso.domain.auth;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "target", schema = "auth_rbac")
public class TargetRole {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "app_id")
    private Long appid;
    @Column(name = "action")
    private String action;
    @Column(name = "object")
    private String object;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "target_role",
            schema = "auth_rbac",
            joinColumns = @JoinColumn(name = "target_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

}
