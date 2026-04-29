package co.kr.allpick.domain.seller.dto;

import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@Schema(description = "판매자 상품 응답 DTO")
public class SellerProductResponseDto {

    @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productName;

    @Schema(description = "가격", requiredMode = Schema.RequiredMode.REQUIRED)
    private int price;

    @Schema(description = "상품 상태", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "승인 상태", requiredMode = Schema.RequiredMode.REQUIRED)
    private String approvalStatus;

    @Schema(description = "썸네일 URL")
    private String thumbnailUrl;
}