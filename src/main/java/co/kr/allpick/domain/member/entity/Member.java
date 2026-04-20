package co.kr.allpick.domain.member.entity;

import java.time.LocalDateTime;
import co.kr.allpick.global.config.JwtUserInfoDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(name = "user_name", nullable = false, length = 50)
    private String name;

    @Column(name = "user_phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "user_address", nullable = false, length = 50)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.BUYER;

    @Column(nullable = false)
    private Integer status = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType = LoginType.LOCAL;

    @Column(name = "social_id", unique = true, length = 255)
    private String socialId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public enum Role {
        SELLER, BUYER
    }

    public enum LoginType {
        LOCAL, KAKAO, NAVER, GOOGLE
    }

    // 일반 회원가입 (BUYER)
    public static Member createLocal(String email, String password, String name, String phone, String address) {
        Member member = new Member();
        member.email = email;
        member.password = password;
        member.name = name;
        member.phone = phone;
        member.address = address;
        member.loginType = LoginType.LOCAL;
        member.role = Role.BUYER;
        member.status = 1;
        return member;
    }

    // 판매자 회원가입 (SELLER)
    public static Member createSeller(String email, String password, String name, String phone, String address) {
        Member member = new Member();
        member.email = email;
        member.password = password;
        member.name = name;
        member.phone = phone;
        member.address = address;
        member.loginType = LoginType.LOCAL;
        member.role = Role.SELLER;  // ← SELLER로 설정
        member.status = 1;
        return member;
    }

    // 소셜 로그인 생성 메서드
    public static Member createSocial(String email, String name, LoginType loginType, String socialId) {
        Member member = new Member();
        member.email = email;
        member.name = name;
        member.loginType = loginType;
        member.socialId = socialId;
        member.role = Role.BUYER;
        member.status = 1;
        return member;
    }

    // 정보 수정
    public void update(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    // 판매자로 권한 변경
    public void upgradeToSeller() {
        this.role = Role.SELLER;
        this.updatedAt = LocalDateTime.now();
    }

    // 탈퇴 (Soft Delete)
    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.status = 0;
        this.updatedAt = LocalDateTime.now();
    }

    // JWT 변환
    public JwtUserInfoDto toJwtUserInfoDto() {
        return new JwtUserInfoDto(this.id, this.email, this.role.name());
    }
}