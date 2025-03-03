package com.qingyou.sso.domain.auth;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_role", schema = "auth_rbac")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "app_id")
    private Long appid;
    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
}
