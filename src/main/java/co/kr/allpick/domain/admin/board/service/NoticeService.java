package co.kr.allpick.domain.admin.board.service;

import co.kr.allpick.domain.admin.board.dto.NoticeCreateRequestDto;
import co.kr.allpick.domain.admin.board.dto.NoticeResponseDto;

import java.util.List;

public interface NoticeService {

    // 공지사항 등록
    NoticeResponseDto createNotice(Long adminId, NoticeCreateRequestDto request);

    // 공지사항 전체 조회
    List<NoticeResponseDto> getAllNotices();

    // 공지사항 단건 조회
    NoticeResponseDto getNoticeById(Long noticeId);

    // 공지사항 수정
    NoticeResponseDto updateNotice(Long noticeId, NoticeCreateRequestDto request);

    // 공지사항 삭제
    void deleteNotice(Long noticeId);
}
