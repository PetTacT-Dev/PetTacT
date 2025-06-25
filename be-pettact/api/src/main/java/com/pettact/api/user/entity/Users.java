package com.pettact.api.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.core.base.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class Users extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private Long userNo;

    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "user_password", nullable = true) // 소셜 로그인은 패스워드 없음
    private String userPassword;

    @Column(name = "user_name", nullable = true)
    private String userName;

    @Column(name = "provider")
    private String provider;     // google, kakao, naver

    @Column(name = "provider_id")
    private String providerId;   // 해당 플랫폼에서 받은 고유 ID

    @Column(name = "user_nickname")
    private String userNickname;

    @Column(name = "user_tel", nullable = true)
    private String userTel;

    @Column(name = "user_birth")
    private LocalDate userBirth;

    @Column(name = "user_zipcode")
    private String userZipcode;

    @Column(name = "user_street1")
    private String userStreet1;

    @Column(name = "user_detail_address")
    private String userDetailAddress;

    @Column(name = "user_has_pet", nullable = false)
    @ColumnDefault("false")
    private Boolean userHasPet;

    @Column(name = "user_email_checked", nullable = false)
    @ColumnDefault("true")
    private Boolean userEmailChecked;

    @Column(name = "user_blacklist", nullable = false)
    @ColumnDefault("false")
    private Boolean userBlacklist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_code", nullable = false)
    private CommonCode roleCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code", nullable = false)
    private CommonCode statusCode;
    
    // 회원 권한 코드(String ex.ROLE_ADMIN)
    public String getRoleCode() {
        return roleCode != null ? roleCode.getCodeId() : null;
    }

    // 회원 상태 코드(String ex.STATUS_ACTIVE)
    public String getStatusCode() {
        return statusCode != null ? statusCode.getCodeId() : null;
    }
}
