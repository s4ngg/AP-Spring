package co.kr.allpick.domain.admin.product.repository;

import co.kr.allpick.domain.admin.product.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
