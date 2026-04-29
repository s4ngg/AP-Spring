package co.kr.allpick.domain.admin.board.service.impl;

import co.kr.allpick.domain.admin.board.dto.NoticeCreateRequestDto;
import co.kr.allpick.domain.admin.board.dto.NoticeResponseDto;
import co.kr.allpick.domain.admin.board.entity.Notice;
import co.kr.allpick.domain.admin.board.repository.NoticeRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoticeServiceImplTest {

    @Mock
    NoticeRepository noticeRepository;

    @InjectMocks
    NoticeServiceImpl noticeService;

    private Notice buildMockNotice() {
        return Notice.builder()
                .adminId(1L)
                .title("서비스 점검 안내")
                .content("4월 30일 오전 2시~4시 서비스 점검이 진행됩니다.")
                .isFixed(false)
                .imageUrl(null)
                .build();
    }

    private Notice buildMockFixedNotice() {
        return Notice.builder()
                .adminId(1L)
                .title("공지사항 고정 테스트")
                .content("고정 공지사항입니다.")
                .isFixed(true)
                .imageUrl(null)
                .build();
    }

    @Test
    @DisplayName("공지사항 등록 성공")
    void 공지사항_등록_성공() {
        // given
        NoticeCreateRequestDto request = new NoticeCreateRequestDto(
                "서비스 점검 안내", "4월 30일 오전 2시~4시 서비스 점검이 진행됩니다.", false, null);
        Notice mockNotice = buildMockNotice();

        when(noticeRepository.save(any(Notice.class))).thenReturn(mockNotice);

        // when
        NoticeResponseDto result = noticeService.createNotice(1L, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAdminId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("서비스 점검 안내");
        assertThat(result.isFixed()).isFalse();
    }

    @Test
    @DisplayName("공지사항 전체 조회 성공 - 고정 공지 우선 정렬")
    void 공지사항_전체_조회_성공() {
        // given
        Notice normal = buildMockNotice();
        Notice fixed = buildMockFixedNotice();

        when(noticeRepository.findByDeletedAtIsNullOrderByIsFixedDescCreatedAtDesc())
                .thenReturn(List.of(fixed, normal));

        // when
        List<NoticeResponseDto> result = noticeService.getAllNotices();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).isFixed()).isTrue();
        assertThat(result.get(1).isFixed()).isFalse();
    }

    @Test
    @DisplayName("공지사항 상세 조회 성공 - 조회수 증가")
    void 공지사항_상세_조회_성공() {
        // given
        Long noticeId = 1L;
        Notice mockNotice = buildMockNotice();

        when(noticeRepository.findByNoticeIdAndDeletedAtIsNull(noticeId))
                .thenReturn(Optional.of(mockNotice));

        // when
        NoticeResponseDto result = noticeService.getNoticeById(noticeId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getViewCount()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("서비스 점검 안내");
    }

    @Test
    @DisplayName("공지사항 상세 조회 실패 - 공지사항 없음")
    void 공지사항_상세_조회_실패_없음() {
        // given
        when(noticeRepository.findByNoticeIdAndDeletedAtIsNull(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> noticeService.getNoticeById(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NOTICE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("공지사항 수정 성공")
    void 공지사항_수정_성공() {
        // given
        Long noticeId = 1L;
        Notice mockNotice = buildMockNotice();
        NoticeCreateRequestDto request = new NoticeCreateRequestDto(
                "수정된 제목", "수정된 본문", true, null);

        when(noticeRepository.findByNoticeIdAndDeletedAtIsNull(noticeId))
                .thenReturn(Optional.of(mockNotice));

        // when
        NoticeResponseDto result = noticeService.updateNotice(noticeId, request);

        // then
        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getContent()).isEqualTo("수정된 본문");
        assertThat(result.isFixed()).isTrue();
    }

    @Test
    @DisplayName("공지사항 수정 실패 - 공지사항 없음")
    void 공지사항_수정_실패_없음() {
        // given
        NoticeCreateRequestDto request = new NoticeCreateRequestDto(
                "수정된 제목", "수정된 본문", false, null);

        when(noticeRepository.findByNoticeIdAndDeletedAtIsNull(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> noticeService.updateNotice(999L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NOTICE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("공지사항 삭제 성공")
    void 공지사항_삭제_성공() {
        // given
        Long noticeId = 1L;
        Notice mockNotice = buildMockNotice();

        when(noticeRepository.findByNoticeIdAndDeletedAtIsNull(noticeId))
                .thenReturn(Optional.of(mockNotice));

        // when
        noticeService.deleteNotice(noticeId);

        // then
        assertThat(mockNotice.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("공지사항 삭제 실패 - 공지사항 없음")
    void 공지사항_삭제_실패_없음() {
        // given
        when(noticeRepository.findByNoticeIdAndDeletedAtIsNull(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> noticeService.deleteNotice(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NOTICE_NOT_FOUND.getMessage());
    }
}
