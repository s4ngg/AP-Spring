package co.kr.allpick.domain.product.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.kr.allpick.domain.product.dto.ProductReqDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// 상품
public class Product {
	// 상품 기본키
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)	// 상품 고유 번호
	private Long productId;
	@Column(name = "seller_id")		// 판매자 고유 번호
	private String sellerId;
	@Column(name = "category_id")	// 카테고리 번호		
	private String categoryId;
	@Column(name = "product_name")	// 상품명		
	private String productName;
	@Column(name = "description")	// 상품 설명	
	private String description;
	@Column(name = "price")			// 상품 가격
	private BigDecimal price;
	@Column(name = "stock_quantity")	// 재고 수량
	private Long stockQuantity;
	@Builder.Default
	@Column(name = "view_count")		// 조회수
	private Long viewCount = 0L;
	@Column(name = "thumnail_url")		// 썸네일 경로
	private String thumbnailUrl;
	@Builder.Default
	@Column(name = "status")			// 판매 상태
	private String status = "ON_SALES";
	@Builder.Default
	@Column(name = "created_at")		// 등록일		( 기본값은 : 현재시간)
	private LocalDateTime createdAt = LocalDateTime.now();
	@Column(name = "update_at")			// 수정일
	private LocalDateTime updatedAt;		
	@Column(name = "deleted_at")		// 삭제일 
	private LocalDateTime deletedAt;
	@Column(name = "description_detail")	// 상세 정보
	private String descriptionDetail;
	
	// 상품 객체 생성	(일단 임시로 모두 값 넣어주기.)
	public static Product ToEntity(ProductReqDto reqDto) {
		return Product.builder()
				.sellerId(reqDto.getSellerId())
				.categoryId(reqDto.getCategoryId())
				.productName(reqDto.getProductName())
				.description(reqDto.getDescription())
				.price(reqDto.getPrice())
				.stockQuantity(reqDto.getStockQuantity())
				.thumbnailUrl(reqDto.getThumbnailUrl())
				.descriptionDetail(reqDto.getDescriptionDetail())
				.build();
	}
	// 상품 수정하기 (존재 검증은 서비스에서...)
	public void updateProduct(Long productId, ProductReqDto reqDto) {
				this.categoryId = reqDto.getCategoryId();
				this.productName = reqDto.getProductName();
				this.description = reqDto.getDescription();
				this.price = reqDto.getPrice();
				this.stockQuantity = reqDto.getStockQuantity();
				this.thumbnailUrl = reqDto.getThumbnailUrl();
				this.descriptionDetail = reqDto.getDescriptionDetail();
				this.updatedAt = LocalDateTime.now();
	}    
	
}
 





