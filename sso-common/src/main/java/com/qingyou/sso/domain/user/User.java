package com.qingyou.sso.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user", schema = "sso_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "id", sequenceName = "user_id_seq")
    @Column(name = "id")
    private Long id;
    @Column(name = "name", nullable = true)
    private String name;
    @NaturalId
    @Column(name = "email", unique = true)
    private String email;
    @NaturalId
    @Column(name = "email", unique = true)
    private String phone;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Account account;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private List<UserInfo> userInfo;
}
