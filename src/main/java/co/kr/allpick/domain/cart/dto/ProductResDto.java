package co.kr.allpick.domain.cart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.kr.allpick.domain.cart.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResDto {
    private String sellerId;          // 판매자 고유 번호
    private String categoryId;        // 카테고리 번호		
    private String productName;       // 상품명		
    private String description;       // 상품 설명	
    private BigDecimal price;         // 상품 가격
    private Long stockQuantity;       // 재고 수량
    private Long viewCount;           // 조회수
    private String thumbnailUrl;       // 썸네일 경로
    private String status;            // 판매 상태
    private LocalDateTime createdAt;   // 등록일
    private LocalDateTime updatedAt;   // 수정일
    private LocalDateTime deletedAt;  // 삭제일 
    private String descriptionDetail; // 상세 정보
    
    
    
    // 매개변수로 들어온 엔티티 객체를 -> 응답객체로 변환하여 , 반환
    public static ProductResDto fromEntity(Product product) {
    	return ProductResDto.builder()
    			.sellerId(product.getSellerId())
    			.categoryId(product.getCategoryId())
    			.productName(product.getProductName())
    			.description(product.getDescription())
    			.price(product.getPrice())   
    			.stockQuantity(product.getStockQuantity())
    			.viewCount(product.getViewCount())
    			.status(product.getStatus())
    			.createdAt(product.getCreatedAt())
    			.updatedAt(product.getUpdatedAt())
    			.deletedAt(product.getDeletedAt())
    			.descriptionDetail(product.getDescriptionDetail())
    			.build();	
    }
}



