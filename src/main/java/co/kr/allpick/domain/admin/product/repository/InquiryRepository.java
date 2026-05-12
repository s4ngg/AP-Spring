package co.kr.allpick.domain.admin.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.kr.allpick.domain.admin.product.entity.Inquiry;
import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 회원별 문의 목록 조회 (삭제 안된 것만, 최신순)
    List<Inquiry> findByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long memberId);

    // 전체 문의 목록 조회 (삭제 안된 것만, 최신순)
    List<Inquiry> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    // 판매자 상품에 달린 문의 목록 조회 (최신순)
    List<Inquiry> findByProductIdInAndDeletedAtIsNullOrderByCreatedAtDesc(List<Long> productIds);

}

