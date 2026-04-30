package co.kr.allpick.domain.seller.apply.dto;

import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "판매자 신청 상태 응답 DTO")
public class SellerApplyStatusResponseDto {

    @Schema(description = "판매자 ID")
    private Long sellerId;

    @Schema(description = "상호명")
    private String businessName;

    @Schema(description = "사업자등록번호")
    private String businessNumber;

    @Schema(description = "대표자명")
    private String representativeName;

    @Schema(description = "은행명")
    private String bankName;

    @Schema(description = "계좌번호")
    private String bankAccount;

    @Schema(description = "신청 상태")
    private SellerStatus status;

    @Schema(description = "신청일")
    private LocalDateTime createdAt;

    public static SellerApplyStatusResponseDto of(Seller seller) {
        return SellerApplyStatusResponseDto.builder()
                .sellerId(seller.getSellerId())
                .businessName(seller.getBusinessName())
                .businessNumber(seller.getBusinessNumber())
                .representativeName(seller.getRepresentativeName())
                .bankName(seller.getBankName())
                .bankAccount(seller.getBankAccount())
                .status(seller.getStatus())
                .createdAt(seller.getCreatedAt())
                .build();
    }
}