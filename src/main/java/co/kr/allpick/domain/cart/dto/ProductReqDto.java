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

public class ProductReqDto {
	
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
	
	
		
	}







