package co.kr.allpick.domain.seller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "판매자 정보 수정 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerUpdateRequestDto {

    @NotBlank
    @Schema(description = "상호명", example = "나이키 코리아",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessName;

    @NotBlank
    @Schema(description = "대표자명", example = "홍길동",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String representativeName;

    @Schema(description = "은행명", example = "국민은행",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bankName;

    @Schema(description = "계좌번호", example = "12345678901234",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bankAccount;
}