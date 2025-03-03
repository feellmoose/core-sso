package com.qingyou.sso.domain.user;

import com.qingyou.sso.api.constants.DataType;
import com.qingyou.sso.api.constants.PlatformType;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_info", schema = "sso_user")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "id", sequenceName = "user_info_id_seq")
    @Column(name = "id")
    private Long id;
    @Column(name = "metadata", nullable = false)
    private String metadata;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "data_type",nullable = false)
    private DataType dataType;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "platform_type", nullable = false)
    private PlatformType platformType;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
