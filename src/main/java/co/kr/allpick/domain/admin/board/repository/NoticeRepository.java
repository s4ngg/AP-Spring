package co.kr.allpick.domain.admin.board.repository;

import co.kr.allpick.domain.admin.board.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByDeletedAtIsNullOrderByFixedDescCreatedAtDesc();

    Optional<Notice> findByNoticeIdAndDeletedAtIsNull(Long noticeId);

    @Modifying
    @Query("UPDATE Notice n SET n.viewCount = n.viewCount + 1 WHERE n.noticeId = :noticeId")
    void increaseViewCount(@Param("noticeId") Long noticeId);
}
