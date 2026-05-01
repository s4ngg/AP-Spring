package co.kr.allpick.domain.admin.board.dto;

import co.kr.allpick.domain.admin.board.entity.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "공지사항 응답 DTO")
public class NoticeResponseDto {

    @Schema(description = "공지사항 ID", example = "1")
    private Long noticeId;

    @Schema(description = "관리자 ID", example = "1")
    private Long adminId;

    @Schema(description = "공지사항 제목", example = "서비스 점검 안내")
    private String title;

    @Schema(description = "공지사항 본문", example = "4월 30일 오전 2시~4시 서비스 점검이 진행됩니다.")
    private String content;

    @Schema(description = "고정 공지 여부", example = "false")
    private boolean fixed;

    @Schema(description = "공지사항 이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "조회수", example = "100")
    private int viewCount;

    @Schema(description = "등록일시", example = "2026-04-30T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2026-04-30T00:00:00")
    private LocalDateTime updatedAt;

    public static NoticeResponseDto from(Notice notice) {
        return NoticeResponseDto.builder()
                .noticeId(notice.getNoticeId())
                .adminId(notice.getAdmin().getAdminId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .fixed(notice.isFixed())
                .imageUrl(notice.getImageUrl())
                .viewCount(notice.getViewCount())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}
