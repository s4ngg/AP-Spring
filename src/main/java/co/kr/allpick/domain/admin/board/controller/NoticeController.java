package co.kr.allpick.domain.admin.board.controller;

import co.kr.allpick.domain.admin.board.controller.docs.NoticeControllerDocs;
import co.kr.allpick.domain.admin.board.dto.NoticeCreateRequestDto;
import co.kr.allpick.domain.admin.board.dto.NoticeResponseDto;
import co.kr.allpick.domain.admin.board.service.NoticeService;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController implements NoticeControllerDocs {

    private final NoticeService noticeService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<NoticeResponseDto>> createNotice(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @RequestBody @Valid NoticeCreateRequestDto request) {
        return ApiResponse.success("공지사항이 등록되었습니다.", noticeService.createNotice(adminInfo.getAdminId(), request));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeResponseDto>>> getAllNotices() {
        return ApiResponse.success("공지사항 목록 조회 성공.", noticeService.getAllNotices());
    }

    @Override
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> getNoticeById(
            @PathVariable("noticeId") Long noticeId) {
        return ApiResponse.success("공지사항 조회 성공.", noticeService.getNoticeById(noticeId));
    }

    @Override
    @PutMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> updateNotice(
            @PathVariable("noticeId") Long noticeId,
            @RequestBody @Valid NoticeCreateRequestDto request) {
        return ApiResponse.success("공지사항이 수정되었습니다.", noticeService.updateNotice(noticeId, request));
    }

    @Override
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable("noticeId") Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ApiResponse.success("공지사항이 삭제되었습니다.", null);
    }
}
