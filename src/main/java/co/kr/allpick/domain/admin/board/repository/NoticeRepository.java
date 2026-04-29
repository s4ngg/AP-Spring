package co.kr.allpick.domain.admin.board.repository;

import co.kr.allpick.domain.admin.board.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByDeletedAtIsNullOrderByIsFixedDescCreatedAtDesc();

    Optional<Notice> findByNoticeIdAndDeletedAtIsNull(Long noticeId);
}
