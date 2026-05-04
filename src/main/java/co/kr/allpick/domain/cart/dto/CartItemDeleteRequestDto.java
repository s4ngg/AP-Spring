package co.kr.allpick.domain.cart.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "장바구니 선택 삭제 요청")
public class CartItemDeleteRequestDto {
	@Schema(description = "삭제할 장바구니 아이템 ID 리스트", example = "[1, 2, 3]")
	private List<Long> cartItemIds;
}
