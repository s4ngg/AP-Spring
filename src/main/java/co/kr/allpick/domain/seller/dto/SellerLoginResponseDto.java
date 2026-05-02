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

    @Schema(description = "판매자 ID", example = "1")
    private Long sellerId;

    @Schema(description = "상호명", example = "테스트상점")
    private String businessName;

    @Schema(description = "대표자명", example = "홍길동")
    private String representativeName;

    @Schema(description = "승인 상태", example = "PENDING")
    private SellerStatus status;

    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
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