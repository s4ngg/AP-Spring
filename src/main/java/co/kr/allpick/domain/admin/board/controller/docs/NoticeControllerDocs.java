package co.kr.allpick.domain.admin.board.controller.docs;

import co.kr.allpick.domain.admin.board.dto.NoticeCreateRequestDto;
import co.kr.allpick.domain.admin.board.dto.NoticeResponseDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Notice", description = "공지사항 API")
public interface NoticeControllerDocs {

    @Operation(summary = "공지사항 등록", description = "관리자가 공지사항을 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<ApiResponse<NoticeResponseDto>> createNotice(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @RequestBody @Valid NoticeCreateRequestDto request);

    @Operation(summary = "공지사항 전체 조회", description = "등록된 모든 공지사항을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공")
    ResponseEntity<ApiResponse<List<NoticeResponseDto>>> getAllNotices();

    @Operation(summary = "공지사항 단건 조회", description = "공지사항 ID로 상세 내용을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 공지사항")
    })
    ResponseEntity<ApiResponse<NoticeResponseDto>> getNoticeById(
            @Parameter(description = "공지사항 ID") @PathVariable("noticeId") Long noticeId);

    @Operation(summary = "공지사항 수정", description = "관리자가 공지사항을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 공지사항")
    })
    ResponseEntity<ApiResponse<NoticeResponseDto>> updateNotice(
            @Parameter(description = "공지사항 ID") @PathVariable("noticeId") Long noticeId,
            @RequestBody @Valid NoticeCreateRequestDto request);

    @Operation(summary = "공지사항 삭제", description = "관리자가 공지사항을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 공지사항")
    })
    ResponseEntity<ApiResponse<Void>> deleteNotice(
            @Parameter(description = "공지사항 ID") @PathVariable("noticeId") Long noticeId);
}
