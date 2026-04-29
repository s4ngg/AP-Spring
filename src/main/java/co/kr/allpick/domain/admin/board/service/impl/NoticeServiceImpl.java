package co.kr.allpick.domain.admin.board.service.impl;

import co.kr.allpick.domain.admin.board.dto.NoticeCreateRequestDto;
import co.kr.allpick.domain.admin.board.dto.NoticeResponseDto;
import co.kr.allpick.domain.admin.board.entity.Notice;
import co.kr.allpick.domain.admin.board.repository.NoticeRepository;
import co.kr.allpick.domain.admin.board.service.NoticeService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private static final Logger logger = LogManager.getLogger(NoticeServiceImpl.class);

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public NoticeResponseDto createNotice(Long adminId, NoticeCreateRequestDto request) {
        Notice notice = Notice.builder()
                .adminId(adminId)
                .title(request.getTitle())
                .content(request.getContent())
                .isFixed(request.isFixed())
                .imageUrl(request.getImageUrl())
                .build();
        noticeRepository.save(notice);
        logger.info("[NoticeService] 공지사항 등록 완료 - adminId: {}", adminId);
        return NoticeResponseDto.from(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeResponseDto> getAllNotices() {
        return noticeRepository.findByDeletedAtIsNullOrderByIsFixedDescCreatedAtDesc()
                .stream()
                .map(NoticeResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public NoticeResponseDto getNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findByNoticeIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));
        notice.increaseViewCount();
        return NoticeResponseDto.from(notice);
    }

    @Override
    @Transactional
    public NoticeResponseDto updateNotice(Long noticeId, NoticeCreateRequestDto request) {
        Notice notice = noticeRepository.findByNoticeIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));
        notice.update(request.getTitle(), request.getContent(), request.isFixed(), request.getImageUrl());
        logger.info("[NoticeService] 공지사항 수정 완료 - noticeId: {}", noticeId);
        return NoticeResponseDto.from(notice);
    }

    @Override
    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findByNoticeIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));
        notice.delete();
        logger.info("[NoticeService] 공지사항 삭제 완료 - noticeId: {}", noticeId);
    }
}
