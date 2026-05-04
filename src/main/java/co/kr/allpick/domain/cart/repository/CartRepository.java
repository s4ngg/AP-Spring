package co.kr.allpick.domain.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.cart.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{

	// 사용자Id로 장바구니 조회
	Optional<Cart> findByMemberId(Long memberId);
}
