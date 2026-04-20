package co.kr.allpick.domain.member.entity;

import java.time.LocalDateTime;

import javax.management.relation.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(length = 255) // 소셜 로그인은 null 가능
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
    @Column(name = "login_Type", nullable = false)
    private LoginType loginType = LoginType.LOCAL;
    
    @Column(name = "social_id", unique = true, length = 255)
    private String socialId;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name ="updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    public enum Role {
    	SELEER, BUYER
    }
    
    // 로그인 유형 ENUM
    public enum LoginType {
    	LOCAL, KAKAO, NAVER, GOOGLE
    }
    
    // 로컬 회원가입 생성 메서드
    public static Member createLocal(String email, String password, String name, String phone, String address) {
    	Member member = new Member();
    	member.email = email;
    	member.password = password;
    	member.name = name;
    	member.phone = phone;
    	member.address = address;
    	member.loginType = LoginType.LOCAL;
    	member.role = Role.BUYER;
    	member.status = 1; // 가입 시 활성
    	return member;
    }
    	//소셜 로그인 생성 메서드
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
    	public void update(String name, String phone, String address) {
    		this.name = name;
    		this.phone = phone;
    		this.address = address;
    		this.updatedAt = LocalDateTime.now();
    	}
    	
    	// 탈퇴 (Soft Delete)
    	public void delete() {
    		this.deletedAt = LocalDateTime.now();
    		this.status = 0;
    		this.updatedAt = LocalDateTime.now();
    }
    
    // 정적 팩토리 메서드
    public static Member create(String email, String password, String name, String phone) {
        Member member = new Member();
        member.email = email;
        member.password = password;
        member.name = name;
        member.phone = phone;
        return member;
    }
}