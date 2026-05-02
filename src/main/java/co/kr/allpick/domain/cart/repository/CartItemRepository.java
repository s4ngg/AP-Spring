package co.kr.allpick.domain.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import co.kr.allpick.domain.cart.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long>{
	// 사용자 한명이 장바구니에 담은 모든 물건 목록 조회
	// 매개변수로 들어온 회원 아이디로, 장바구니 테이블로 연결하여 사용자를 특정시키기 (where)
	// cartItem 엔티티에서 특정된 사용자의 상품정보랑, 상품옵션 정보를 한번에 조인하여서 조회하기
	@Query("SELECT ci FROM CartItem ci " +
		   "join fetch ci.product p " +
		   "join fetch ci.productOption po " +
		   "where ci.cart.member.id = :memberId " 
			)
	List<CartItem> findByCartList(@Param("memberId") Long memberId);
}
