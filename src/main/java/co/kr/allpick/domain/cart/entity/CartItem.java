package co.kr.allpick.domain.cart.entity;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.entity.ProductOption;
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
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_option_id",nullable = false)
	private ProductOption productOption;
	
	@Column(name = "quantity", nullable = false)
	private Integer quantity;
	
	 
	
	
	public static CartItem addToCart(Product product, Cart cart, Member member, 
										ProductOption productOption,int quantity) {
		return CartItem.builder()
				.product(product)
				.cart(cart)
				.member(member)
				.productOption(productOption)
				.quantity(quantity)
				.build();
	} 
	
	
	public void addQuantityAtProductDetail(int quantity) {
		this.quantity += quantity;
	}
	
	public void upadateQuantityAtCart(int quantity) {
		this.quantity = quantity;
	}
}
