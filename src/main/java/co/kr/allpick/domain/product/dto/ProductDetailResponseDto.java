package co.kr.allpick.domain.product.dto;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Schema(description = "상품 상세페이지 요청 DTO")
public class ProductDetailResponseDto {
	@NotNull
	@Schema(description = "상품 Id", example ="1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long productId;							
	@NotBlank
	@Schema(description = "카테고리명", example ="주류", requiredMode = Schema.RequiredMode.REQUIRED)
	private String parentCategoryName;				
	@NotBlank
	@Schema(description = "브랜드명", example ="나이키", requiredMode = Schema.RequiredMode.REQUIRED)
	private String brand;							
	@NotBlank
	@Schema(description = "상품명", example ="신발", requiredMode = Schema.RequiredMode.REQUIRED)
	private String productName;						
	@NotBlank
	@Schema(description = "대표이미지url", example ="\"https://allpick.com\"", requiredMode = Schema.RequiredMode.REQUIRED)
	private String thumbnailUrl;					
	
	
	
	@NotNull
	@PositiveOrZero
	@Schema(description = "판매가격", example ="25000", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal price;		
	
	@NotBlank
	@Schema(description = "상품 상세 설명", example ="나이키 에어맥스 97의 클래식 화이트 컬러입니다. "
									   , requiredMode = Schema.RequiredMode.REQUIRED)
	private String description;						
	@NotBlank
	@Schema(description = "제조사", example ="나이키 코리아", requiredMode = Schema.RequiredMode.REQUIRED)
	private String manufacturer;					
	@NotBlank
	@Schema(description = "원산지", example ="베트남", requiredMode = Schema.RequiredMode.REQUIRED)
	private String origin;							
	@NotBlank
	@Schema(description = "주의사항", example ="직사광선을 피해 보관하세요", requiredMode = Schema.RequiredMode.REQUIRED)
	private String precaution;						
	
	@Schema(description = "상품옵션 리스트")
	private List<ProductOptionResponseDto> optionList;
	@Schema(description = "상품이미지 리스트")
	private List<ProductImageResponseDto> productImagesList;				
	@Schema(description = "상품 리뷰 리스트")
	private Page<ReviewResponseDto> reviewList;
	
	public static ProductDetailResponseDto from(Product product, Page<ReviewResponseDto> reviewList) {
		return ProductDetailResponseDto.builder()
				.productId(product.getProductId())
				.parentCategoryName(product.getChildCategory() != null && product.getChildCategory().getParentCategory() != null ?
						product.getChildCategory().getParentCategory().getCategoryName() : "미분류")
				.brand(product.getBrand())
				.productName(product.getProductName())
				.thumbnailUrl(product.getThumbnailUrl())
				.price(product.getPrice())
				.description(product.getDescription())
				.manufacturer(product.getManufacturer())
				.origin(product.getOrigin()) 
				.precaution(product.getPrecaution())
				.optionList(product.getOptionList().stream()
						.filter(o -> o.getDeletedAt() == null)
						.map(ProductOptionResponseDto::from)
						.toList())
				.productImagesList(product.getProductImageList().stream()
						.filter(i -> i.getDeletedAt() == null)
						.map(ProductImageResponseDto::from)
						.toList()) 
				.reviewList(reviewList)  
 				.build();    
	}
} 
