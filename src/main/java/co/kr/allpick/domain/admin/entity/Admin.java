package co.kr.allpick.domain.admin.entity;


import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String adminName;

    @Column(nullable = false)
    private String adminPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable= false)
    private AdminRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable= false)
    private AdminStatus status;

    private LocalDateTime lastLoginAt;


    @Builder
    public Admin( String email, String password, String adminName
            , String adminPhone, AdminRole role, AdminStatus status) {
        this.email = email;
        this.password = password;
        this.adminName =adminName;
        this.adminPhone = adminPhone;
        this.role = role;
        this.status =status;
    }


    public void updateStatus(AdminStatus status){
        this.status =status;
    }

    public void updateLastLoginAt(LocalDateTime lastLoginAt){
        this.lastLoginAt =lastLoginAt;
    }


    public enum AdminRole{
        SUPER_ADMIN, CS_ADMIN
    }

    public enum AdminStatus{
        ACTIVE, BLOCKED
    }

}