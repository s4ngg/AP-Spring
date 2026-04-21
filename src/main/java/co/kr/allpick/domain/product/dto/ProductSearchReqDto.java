package co.kr.allpick.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchReqDto {
	// 이름 검색
	private String productName;
}
