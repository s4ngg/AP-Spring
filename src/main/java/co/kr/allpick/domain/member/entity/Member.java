package co.kr.allpick.domain.member.entity;

import co.kr.allpick.global.common.BaseEntity;
import co.kr.allpick.global.config.JwtUserInfoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {

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

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    @Builder.Default
    private LoginType loginType = LoginType.LOCAL;

    @Column(name = "social_id", unique = true, length = 255)
    private String socialId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MemberGrade grade = MemberGrade.NORMAL;

    public enum LoginType {
        LOCAL
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
        member.status = 1;
        return member;
    }

    // 정보 수정
    public void update(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    // 탈퇴 (Soft Delete)
    public void delete() {
        this.status = 0;

    }
    
    public void updateGrade(MemberGrade grade) {
        this.grade = grade;
    }

    // JWT 변환
    public JwtUserInfoDto toJwtUserInfoDto() {
        return new JwtUserInfoDto(this.id, this.email);
    }
}