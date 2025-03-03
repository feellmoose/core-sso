package com.qingyou.sso.domain.oauth;

import com.qingyou.sso.api.constants.DataType;
import com.qingyou.sso.api.constants.PlatformType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "third_party_required_user_info", schema = "sso_oauth")
public class ThirdPartyRequiredUserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "id", sequenceName = "third_party_required_user_info_id_seq")
    @Column(name = "id")
    private Long id;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "data_type",nullable = false)
    private DataType dataType;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "platform_type", nullable = false)
    private PlatformType platformType;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @PrimaryKeyJoinColumn
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "third_party_app_id")
    private ThirdPartyApp thirdPartyApp;
}