package co.kr.allpick.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.allpick.domain.member.seller.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long>{
	boolean existsByEmail(String email);
	java.util.Optional<Seller> findByEmail(String email);

}
