package co.kr.allpick.domain.seller.repository;

import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByMemberId(Long memberId);

    boolean existsByBusinessNumber(String businessNumber);

    Optional<Seller> findByBusinessNumber(String businessNumber);
    
    Optional<Seller> findBySellerIdAndDeletedAtIsNull(Long sellerId);
    
    boolean existsByMemberId(Long memberId);

    Optional<Seller> findByMemberIdAndDeletedAtIsNull(Long memberId);
    
    boolean existsByMemberIdAndDeletedAtIsNull(Long memberId);

}