package com.qingyou.sso.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account", schema = "sso_user")
public class Account {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @NaturalId
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String salt;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
