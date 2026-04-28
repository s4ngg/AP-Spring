package co.kr.allpick.domain.seller.dto;

import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "판매자 로그인 응답 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerLoginResponseDto {

    @Schema(description = "판매자 ID")
    private Long sellerId;

    @Schema(description = "상호명")
    private String businessName;

    @Schema(description = "대표자명")
    private String representativeName;

    @Schema(description = "승인 상태")
    private SellerStatus status;

    @Schema(description = "JWT 토큰")
    private String token;

    public static SellerLoginResponseDto of(Seller seller, String token) {
        return SellerLoginResponseDto.builder()
                .sellerId(seller.getSellerId())
                .businessName(seller.getBusinessName())
                .representativeName(seller.getRepresentativeName())
                .status(seller.getStatus())
                .token(token)
                .build();
    }
}