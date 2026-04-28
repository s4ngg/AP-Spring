package co.kr.allpick.domain.cart.entity;


import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "cart")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Cart extends BaseEntity{
	// 장바구니 기본키
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id", nullable = false)
	private Long cartId;
	// 회원번호 외래키
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id", nullable = false)
	private Member member;
	
	//새로운 장바구니 생성 메서드  (장바구니 id는 자동생성됨)
	public static Cart createCart(Member member) {	
		return Cart.builder()
				.member(member)
				.build();
	}
}

