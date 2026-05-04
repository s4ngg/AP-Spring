package co.kr.allpick.domain.admin.board.controller.docs;

import co.kr.allpick.domain.admin.board.dto.FaqCreateRequestDto;
import co.kr.allpick.domain.admin.board.dto.FaqResponseDto;
import co.kr.allpick.domain.admin.board.entity.Faq;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "FAQ", description = "FAQ API")
public interface FaqControllerDocs {

    @Operation(summary = "FAQ 등록", description = "관리자가 FAQ를 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "FAQ 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<ApiResponse<FaqResponseDto>> createFaq(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @RequestBody @Valid FaqCreateRequestDto request);

    @Operation(summary = "FAQ 전체 조회", description = "등록된 모든 FAQ를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "FAQ 목록 조회 성공")
    ResponseEntity<ApiResponse<List<FaqResponseDto>>> getAllFaqs();

    @Operation(summary = "FAQ 카테고리별 조회", description = "카테고리(DELIVERY, PAYMENT, CANCEL_REFUND, MEMBER)별로 FAQ를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카테고리별 FAQ 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 카테고리")
    })
    ResponseEntity<ApiResponse<List<FaqResponseDto>>> getFaqsByCategory(
            @Parameter(description = "FAQ 카테고리 (DELIVERY, PAYMENT, CANCEL_REFUND, MEMBER)")
            @PathVariable("category") Faq.FaqCategory category);

    @Operation(summary = "FAQ 수정", description = "관리자가 FAQ를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "FAQ 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 FAQ")
    })
    ResponseEntity<ApiResponse<FaqResponseDto>> updateFaq(
            @Parameter(description = "FAQ ID") @PathVariable("faqId") Long faqId,
            @RequestBody @Valid FaqCreateRequestDto request);

    @Operation(summary = "FAQ 노출 여부 변경", description = "관리자가 FAQ의 노출 여부를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "노출 여부 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 FAQ")
    })
    ResponseEntity<ApiResponse<Void>> toggleVisibility(
            @Parameter(description = "FAQ ID") @PathVariable("faqId") Long faqId,
            @Parameter(description = "노출 여부 (true: 노출, false: 숨김)") @RequestParam("isVisible") boolean isVisible);

    @Operation(summary = "FAQ 삭제", description = "관리자가 FAQ를 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "FAQ 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 FAQ")
    })
    ResponseEntity<ApiResponse<Void>> deleteFaq(
            @Parameter(description = "FAQ ID") @PathVariable("faqId") Long faqId);
}
