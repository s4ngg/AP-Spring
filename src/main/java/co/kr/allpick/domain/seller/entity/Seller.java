package co.kr.allpick.domain.seller.entity;

import co.kr.allpick.global.common.BaseEntity;
import co.kr.allpick.global.config.JwtUserInfoDto;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(name = "user_phone", nullable = false, length = 20)
    private String userPhone;

    @Column(name = "user_address", nullable = false, length = 50)
    private String userAddress;

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @Column(name = "business_number", nullable = false, length = 20)
    private String businessNumber;

    private int status;
    
    public JwtUserInfoDto toJwtUserInfoDto() {
        return JwtUserInfoDto.builder()
                .memberId(this.id)
                .email(this.email)
                .role("SELLER")
                .build();
    }
}