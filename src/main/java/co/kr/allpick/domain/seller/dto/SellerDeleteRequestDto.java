package co.kr.allpick.domain.seller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "판매자 삭제 요청 DTO")
public class SellerDeleteRequestDto {

    @NotNull(message = "판매자 ID는 필수입니다.")
    @Schema(description = "판매자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sellerId;
}