package co.kr.allpick.domain.seller.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;

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
  		   " WHERE m.id = :memberId " +
  		   "AND s.deletedAt IS NULL ") 
  	Optional<Seller> findWithMemberByMemberId(@Param("memberId") Long memberId);

    boolean existsByMemberIdAndDeletedAtIsNull(Long memberId);

    List<Seller> findAllByStatusInAndDeletedAtIsNullOrderByCreatedAtDesc(List<SellerStatus> statuses);

    List<Seller> findAllByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(SellerStatus status);
}
