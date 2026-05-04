package co.kr.allpick.domain.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import co.kr.allpick.domain.cart.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long>{
	@Query("SELECT ci FROM CartItem ci " +
	           "JOIN FETCH ci.product p " +
	           "JOIN FETCH ci.productOption po " +
	           "WHERE ci.member.id = :memberId " +
	           "AND ci.deletedAt IS NULL")
	    List<CartItem> findAllActiveByMemberId(@Param("memberId") Long memberId);
}
