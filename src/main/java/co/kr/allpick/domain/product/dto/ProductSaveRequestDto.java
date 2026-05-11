package co.kr.allpick.domain.product.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import co.kr.allpick.domain.product.entity.ParentCategory;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.entity.ProductImage;
import co.kr.allpick.domain.product.entity.ProductOption;
import co.kr.allpick.domain.seller.entity.Seller;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "상품등록 요청Dto : 카테고리가 정해지면 상품생성")
public class ProductSaveRequestDto {
	
		@NotNull
		@Schema(description = "카테고리 id (웹페이지에서 전달)", example = "1", requiredMode = RequiredMode.REQUIRED)
		private Long categoryId;
 
		@NotBlank @Schema(description = "상품명", example = " 에어맥스 97 화이트", requiredMode = RequiredMode.REQUIRED)
	    private String productName;
		
		@NotBlank @Schema(description = "브랜드명", example = "나이키", requiredMode = RequiredMode.REQUIRED)
	    private String brand;
		
		@NotBlank @Schema(	description = "메인이미지 경로", 
							example = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&h=600&fit=crop", 
							requiredMode = RequiredMode.REQUIRED)
	    private String thumbnailUrl;

		@NotBlank @Schema(	description = "간단한 상품 설명",
							example = "나이키 에어맥스 97의 클래식 화이트 컬러입니다....", 
							requiredMode = RequiredMode.REQUIRED)
	    private String description;
	    
		@Schema(description = "상품 가격", example = "27000", requiredMode = RequiredMode.REQUIRED)
		@NotNull
		@PositiveOrZero
		@Positive(message = "가격은 0원 이상이어야 합니다.")
		private BigDecimal price ;

		@NotBlank @Schema(description = "제조사", example = "나이키 코리아", requiredMode = RequiredMode.REQUIRED)
	    private String manufacturer;

		@NotBlank @Schema(description = "원산지", example = "	베트남", requiredMode = RequiredMode.REQUIRED)
	    private String origin;
	    
		@NotBlank @Schema(description = "주의사항", example = "직사광선을 피해 보관하세요.", requiredMode = RequiredMode.REQUIRED)
	    private String precaution;

		
		@NotEmpty
		@Schema(description = "상품옵션 리스트", example = "사이즈:270 등, 재고:5 등...", requiredMode = RequiredMode.REQUIRED)
		private List<ProductOptionRequestDto> optionList = new ArrayList<>();
		@NotEmpty
		@Schema(description = "상품이미지 리스트", example = "Url, 이미지 순서", requiredMode = RequiredMode.REQUIRED)
		private List<ProductImageRequestDto> productImageList = new ArrayList<>();
		
		
		
		@Schema(description = "상품객체 생성 메서드")
		public Product toEntity(Seller seller, ParentCategory parentCategory) {
			Product product = Product.builder()
						.seller(seller)
						.parentCategory(parentCategory)	
						.productName(this.productName)
						.brand(this.brand)
						.thumbnailUrl(this.thumbnailUrl)
						.description(this.description)
						.price(this.price)
						.manufacturer(this.manufacturer)
						.origin(this.origin)
						.precaution(this.precaution)
						.build();
			// 상품이미지 : 옵션 변경요청이 오면, 요청온 것을 상품 객체의 옵션으로 저장
			if(this.optionList != null) {
				List<ProductOption> options = this.optionList.stream()
						.map(option -> option.toEntity(product))
						.toList();
				
				product.getOptionList().addAll(options);
			}
			// 상품이미지 : 이미지 변경 요청이오면 , 이미지 각각을 상품객체의 옵션 리스트에 저장 해주기
			if(this.productImageList != null ) {
				List<ProductImage> images = this.productImageList.stream().map(image -> image.toEntity(product)).toList();
				product.getProductImageList().addAll(images);
			}
			return product;
		}


}




