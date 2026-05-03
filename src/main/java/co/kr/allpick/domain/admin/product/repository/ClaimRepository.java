package co.kr.allpick.domain.admin.product.repository;

import co.kr.allpick.domain.admin.product.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    // 회원별 클레임 목록 조회 (삭제 안된 것만)
    List<Claim> findByMemberIdAndDeletedAtIsNull(Long memberId);

    // 전체 클레임 목록 조회 (삭제 안된 것만)
    List<Claim> findAllByDeletedAtIsNull();

    // 활성 클레임 존재 여부 확인 (취소/거부 제외)
    boolean existsByOrderItemIdAndStatusNotIn(Long orderItemId, List<Claim.ClaimStatus> statuses);
    
    @Query("SELECT c FROM Claim c WHERE c.memberId = :memberId AND c.deletedAt IS NULL")
    List<Claim> findByMemberId(@Param("memberId") Long memberId);
    
    @Query("SELECT c FROM Claim c " +
    	       "JOIN OrderItem oi ON c.orderItemId = oi.id " +
    	       "JOIN oi.product p " +
    	       "JOIN p.seller s " +
    	       "WHERE s.sellerId = :sellerId " +
    	       "AND c.deletedAt IS NULL")
    	List<Claim> findBySellerIdAndDeletedAtIsNull(@Param("sellerId") Long sellerId);
}
