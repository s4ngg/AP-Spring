package co.kr.allpick.domain.seller.dto;

import co.kr.allpick.domain.seller.entity.Seller;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SellerSignupRequestDto {

    private String email;
    private String password;
    private String userName;
    private String userPhone;
    private String userAddress;
    private String businessName;
    private String businessNumber;

    public Seller toEntity(String encodedPassword) {
        return Seller.builder()
                .email(this.email)
                .password(encodedPassword)
                .userName(this.userName)
                .userPhone(this.userPhone)
                .userAddress(this.userAddress)
                .businessName(this.businessName)
                .businessNumber(this.businessNumber)
                .build();
    }
}