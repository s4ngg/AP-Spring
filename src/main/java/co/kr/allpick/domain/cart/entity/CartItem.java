package co.kr.allpick.domain.cart.entity;

import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.entity.ProductOption;
import co.kr.allpick.global.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Table(name = "cartItem")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CartItem extends BaseEntity{
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_item_id", nullable = false) 
	private Long cartItemId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_option_id",nullable = false)
	private ProductOption productOption;
	
	@Column(name = "quantity", nullable = false)
	private Integer quantity;
	
	
	
	@Schema(description = "상세페이지에서 상품 수량 증량하는 메서드")
	public void addQuantityAtProductDetail(int quantity) {
		this.quantity += quantity;
	}
	@Schema(description = "장바구니에서 상품수량을 최종 결정하는 메서드")
	public void addQuantityAtCart(int quantity) {
		this.quantity = quantity;
	}
}
