package co.kr.allpick.domain.seller.entity;

import java.time.LocalDateTime;

import co.kr.allpick.global.common.BaseEntity;
import co.kr.allpick.global.config.JwtUserInfoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "sellers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Seller extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 100)
	private String email;
	
	@Column(length = 255)
	private String password;
	
	@Column(name = "user_name", nullable = false, length = 50)
	private String name;
	
	@Column(name = "user_phone", nullable =false, length = 20)
	private String phone;
	
	@Column(name = "user_address", nullable = false, length = 50)
	private String address;
	
	@Column(name = "business_number", nullable = false, length = 20)
	private String businessNumber;
	
	@Column(name = "business_name", nullable = false, length = 100)
	private String businessName;
	
	@Builder.Default
	@Column(nullable = false)
	private Integer status = 1;

	
	//정보 수정
	public void update(String name, String phone, String address, String businessName) {
		this.name= name;
		this.phone = phone;
		this.address = address;
		this.businessName = businessName;
	}

	// 탈퇴 (Soft Delete)
	public void delete() {
		this.status = 0;
	}
	
	// JWT 변환
	public JwtUserInfoDto toJwtUserInfoDto() {
		return new JwtUserInfoDto(this.id, this.email, "SELLER");
	}
}
