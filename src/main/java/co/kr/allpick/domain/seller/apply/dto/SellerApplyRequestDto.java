package co.kr.allpick.domain.seller.apply.dto;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.seller.entity.Seller;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "판매자 신청 요청 DTO")
public class SellerApplyRequestDto {

    @NotBlank
    @Schema(description = "상호명", example = "나이키 코리아",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessName;

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "사업자등록번호는 10자리 숫자여야 합니다.")
    @Schema(description = "사업자등록번호", example = "1234567890",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessNumber;

    @NotBlank
    @Schema(description = "대표자명", example = "홍길동",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String representativeName;

    @NotBlank
    @Schema(description = "은행명", example = "국민은행",
     
    requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankName;

    @NotBlank
    @Schema(description = "계좌번호", example = "12345678901234",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankAccount;
    
    public Seller toEntity(Member member) {
    	return Seller.builder()
    	.member(member)
    	.businessName(this.businessName)
    	.businessNumber(this.businessNumber)
    	.representativeName(this.representativeName)
    	.bankName(this.bankName)
    	.bankAccount(this.bankAccount)
    	.build();
    	}
}