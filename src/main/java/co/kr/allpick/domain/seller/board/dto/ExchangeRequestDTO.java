package co.kr.allpick.domain.seller.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "교환 요청 DTO")
public class ExchangeRequestDTO {

	@Schema(description = "주문 ID", example = "1")
	@NotNull(message = "주문 ID는 필수입니다.")
	private Long orderId;
	
	@Schema(description = "교환 사유", example = "사이즈 불일치")
	@NotBlank(message = "교환 사유는 필수입니다.")
	@Size(min = 5, max = 500, message = "교환 사유는 5자 이상 500자 이하로 입력해주세요.")
	private String reason;
	
	@Schema(description = "교환 상품 ID", example = "1")
	@NotNull(message = "교환 상품 ID는 필수입니다.")
	private Long productId;
	
	@Schema(description = "교환 수량", example ="1")
	@NotNull(message = "수량은 필수입니다.")
	@Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
	private int quantity;
}
