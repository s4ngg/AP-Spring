package co.kr.allpick.domain.admin.product.repository;

import co.kr.allpick.domain.admin.product.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    // 회원별 클레임 목록 조회 (삭제 안된 것만)
    List<Claim> findByMemberIdAndDeletedAtIsNull(Long memberId);

    // 전체 클레임 목록 조회 (삭제 안된 것만)
    List<Claim> findAllByDeletedAtIsNull();
}
