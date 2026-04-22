package co.kr.allpick.domain.seller.dto;

import co.kr.allpick.domain.seller.entity.Seller;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SellerLoginResponseDto {

    private Long   id;
    private String email;
    private String name;
    private String businessName;
    private String token;

    // SellerAuthService에서 호출하는 of() 메서드
    public static SellerLoginResponseDto of(Seller seller, String token) {
        return SellerLoginResponseDto.builder()
            .id(seller.getId())
            .email(seller.getEmail())
            .name(seller.getName())
            .businessName(seller.getBusinessName())
            .token(token)
            .build();
    }
}