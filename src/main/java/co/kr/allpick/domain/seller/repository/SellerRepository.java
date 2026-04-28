package co.kr.allpick.domain.seller.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import co.kr.allpick.domain.seller.entity.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByEmail(String email);
    boolean existsByEmail(String email);
}