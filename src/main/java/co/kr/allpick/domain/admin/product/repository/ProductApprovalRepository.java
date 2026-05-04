package co.kr.allpick.domain.admin.product.repository;

import co.kr.allpick.domain.admin.product.entity.ProductApproval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductApprovalRepository extends JpaRepository<ProductApproval, Long> {
}
