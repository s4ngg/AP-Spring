package co.kr.allpick.domain.admin.board.controller;

import co.kr.allpick.domain.admin.board.controller.docs.FaqControllerDocs;
import co.kr.allpick.domain.admin.board.dto.FaqCreateRequestDto;
import co.kr.allpick.domain.admin.board.dto.FaqResponseDto;
import co.kr.allpick.domain.admin.board.entity.Faq;
import co.kr.allpick.domain.admin.board.service.FaqService;
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
@RequestMapping("/api/faqs")
public class FaqController implements FaqControllerDocs {

    private final FaqService faqService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<FaqResponseDto>> createFaq(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @RequestBody @Valid FaqCreateRequestDto request) {
        return ApiResponse.success("FAQ가 등록되었습니다.", faqService.createFaq(adminInfo.getAdminId(), request));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<FaqResponseDto>>> getAllFaqs() {
        return ApiResponse.success("FAQ 목록 조회 성공.", faqService.getAllFaqs());
    }

    @Override
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<FaqResponseDto>>> getFaqsByCategory(
            @PathVariable("category") Faq.FaqCategory category) {
        return ApiResponse.success("카테고리별 FAQ 조회 성공.", faqService.getFaqsByCategory(category));
    }

    @Override
    @PutMapping("/{faqId}")
    public ResponseEntity<ApiResponse<FaqResponseDto>> updateFaq(
            @PathVariable("faqId") Long faqId,
            @RequestBody @Valid FaqCreateRequestDto request) {
        return ApiResponse.success("FAQ가 수정되었습니다.", faqService.updateFaq(faqId, request));
    }

    @Override
    @PatchMapping("/{faqId}/visibility")
    public ResponseEntity<ApiResponse<Void>> toggleVisibility(
            @PathVariable("faqId") Long faqId,
            @RequestParam("isVisible") boolean isVisible) {
        faqService.toggleVisibility(faqId, isVisible);
        return ApiResponse.success("FAQ 노출 여부가 변경되었습니다.", null);
    }

    @Override
    @DeleteMapping("/{faqId}")
    public ResponseEntity<ApiResponse<Void>> deleteFaq(
            @PathVariable("faqId") Long faqId) {
        faqService.deleteFaq(faqId);
        return ApiResponse.success("FAQ가 삭제되었습니다.", null);
    }
}
