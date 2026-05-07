package co.kr.allpick.domain.seller.dto.admin;

import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SellerListResponseDto {

    private Long sellerId;
    private Long memberId;
    private String businessName;
    private String businessNumber;
    private String representativeName;
    private String bankName;
    private String bankAccount;
    private SellerStatus status;
    private LocalDateTime createdAt;

    public static SellerListResponseDto from(Seller seller) {
        return SellerListResponseDto.builder()
                .sellerId(seller.getSellerId())
                .memberId(seller.getMember().getId())
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
