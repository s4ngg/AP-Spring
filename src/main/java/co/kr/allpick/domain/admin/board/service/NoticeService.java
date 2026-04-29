package co.kr.allpick.domain.admin.board.service;

import co.kr.allpick.domain.admin.board.dto.NoticeCreateRequestDto;
import co.kr.allpick.domain.admin.board.dto.NoticeResponseDto;

import java.util.List;

public interface NoticeService {

    NoticeResponseDto createNotice(Long adminId, NoticeCreateRequestDto request);

    List<NoticeResponseDto> getAllNotices();

    NoticeResponseDto getNoticeById(Long noticeId);

    NoticeResponseDto updateNotice(Long noticeId, NoticeCreateRequestDto request);

    void deleteNotice(Long noticeId);
}
