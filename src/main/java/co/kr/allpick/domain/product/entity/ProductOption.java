package co.kr.allpick.domain.product.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_options")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProductOption extends BaseEntity{
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "option_id", nullable = false) 
	private Long optionId;					//상품옵션 기본키
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;			// 상품 외래키
	 
	@Column(name="option_name" , length = 50, nullable = false)
	private String optionName;				//옵션명
	
	@Column(name="option_value" , length = 50, nullable = false)
	private String optionValue;				//옵션값
	@Builder.Default
	@Column(name="additional_price" , nullable = false)		
	private Integer additionalPrice = 0;			//가격
	@Builder.Default
	@Column(name="stock_quantity" , nullable = false)			
	private Integer stockQuantity = 0;				//재고
}	
