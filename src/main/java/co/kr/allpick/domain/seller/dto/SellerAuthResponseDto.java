package co.kr.allpick.domain.seller.dto;

import co.kr.allpick.domain.seller.entity.Seller;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "판매자 로그인 응답 DTO")
public class SellerAuthResponseDto {

    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "판매자 이메일", example = "seller@test.com")
    private String email;

    @Schema(description = "판매자 이름", example = "홍길동")
    private String name;

    @Schema(description = "역할", example = "SELLER")
    private String role;

    public static SellerAuthResponseDto of(String token, Seller seller) {
        return SellerAuthResponseDto.builder()
                .token(token)
                .email(seller.getEmail())
                .name(seller.getUserName())
                .role("SELLER")
                .build();
    }
}