package co.kr.allpick.domain.admin.board.repository;

import co.kr.allpick.domain.admin.board.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    // 전체 조회 (삭제(deletedAt이 null인 것) 안 된 것만, 순서대로)
    List<Faq> findAllByDeletedAtIsNullOrderByDisplayOrderAsc();

    // 카테고리 필터 조회 (UI의 배송/결제 탭 클릭시)
    List<Faq> findAllByCategoryAndDeletedAtIsNullOrderByDisplayOrderAsc(Faq.FaqCategory category);

}
