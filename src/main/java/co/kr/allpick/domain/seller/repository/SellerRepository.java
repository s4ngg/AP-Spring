package co.kr.allpick.domain.seller.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.kr.allpick.domain.seller.entity.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByMemberId(Long memberId);

    boolean existsByBusinessNumber(String businessNumber);

    Optional<Seller> findByBusinessNumber(String businessNumber);
    
    Optional<Seller> findBySellerIdAndDeletedAtIsNull(Long sellerId);
    
    boolean existsByMemberId(Long memberId);

    Optional<Seller> findByMemberIdAndDeletedAtIsNull(Long memberId);
    

 // 판매자인지 검증 (새 상품 등록용)
  	@Query("SELECT s FROM Seller s " +
  		   "JOIN FETCH s.member m " + 
  		   " WHERE m.id = :memberId") 
  	Optional<Seller> findWithMemberByMemberId(@Param("memberId") Long memberId);

    boolean existsByMemberIdAndDeletedAtIsNull(Long memberId);
}