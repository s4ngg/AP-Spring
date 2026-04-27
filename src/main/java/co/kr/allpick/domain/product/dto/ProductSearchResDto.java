package co.kr.allpick.domain.product.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResDto {
	private List<ProductResDto> productList;	// 나중에 수정하기
	 
}
	