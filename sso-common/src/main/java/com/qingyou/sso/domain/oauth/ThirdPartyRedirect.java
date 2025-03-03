package com.qingyou.sso.domain.oauth;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "third_party_redirect", schema = "sso_oauth")
public class ThirdPartyRedirect {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "id", sequenceName = "third_party_redirect_id_seq")
    @Column(name = "id")
    private Long id;
    @Column(name = "uri")
    private String URI;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @PrimaryKeyJoinColumn
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "third_party_app_id")
    private ThirdPartyApp thirdPartyApp;
}