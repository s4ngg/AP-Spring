package co.kr.allpick.domain.seller.repository;

import co.kr.allpick.domain.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByBusinessNumber(String businessNumber);

    // status=1 인 활성 판매자만 조회
    Optional<Seller> findByEmailAndStatus(String email, Integer status);
}