package co.kr.allpick.domain.admin.entity;


import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Getter
@NoArgsConstructor
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="admin_id")
    private Long adminId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "admin_name", nullable = false)
    private String adminName;

    @Column(name = "admin_phone", nullable = false)
    private String adminPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AdminStatus status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;


    @Builder
    public Admin( String email, String password, String adminName
            , String adminPhone, AdminStatus status) {
        this.email = email;
        this.password = password;
        this.adminName =adminName;
        this.adminPhone = adminPhone;
        this.status =status;
    }


    public void updateStatus(AdminStatus status){
        this.status =status;
    }

    public void updateLastLoginAt(LocalDateTime lastLoginAt){
        this.lastLoginAt =lastLoginAt;
    }

    // TODO: 공통 JWT 코드 정리 후 제거 예정
    public enum AdminRole{
        SUPER_ADMIN, CS_ADMIN
    }

    public enum AdminStatus{
        ACTIVE, BLOCKED
    }

}
