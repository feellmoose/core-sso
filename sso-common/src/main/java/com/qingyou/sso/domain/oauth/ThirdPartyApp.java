package com.qingyou.sso.domain.oauth;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "third_party_app", schema = "sso_oauth")
public class ThirdPartyApp {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "id", sequenceName = "third_party_app_id_seq")
    @Column(name = "id")
    private Long id;
    @Column(name = "app_name")
    private String appName;
    @Column(name = "client_id", unique = true)
    private String clientId;
    @Column(name = "client_secret", length = 400)
    private String clientSecret;
    @OneToMany(mappedBy = "thirdPartyApp", fetch = FetchType.EAGER)
    private List<ThirdPartyRedirect> redirectURIs;
    @OneToMany(mappedBy = "thirdPartyApp", fetch = FetchType.EAGER)
    private List<ThirdPartyRequiredUserInfo> requiredUserInfos;
}
