package co.kr.allpick.domain.admin.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.kr.allpick.domain.admin.product.entity.Inquiry;
import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 회원별 문의 목록 조회 (삭제 안된 것만)
    List<Inquiry> findByMemberIdAndDeletedAtIsNull(Long memberId);

    // 전체 문의 목록 조회 (삭제 안된 것만)
    List<Inquiry> findAllByDeletedAtIsNull();
}
