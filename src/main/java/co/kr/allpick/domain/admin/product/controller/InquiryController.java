package co.kr.allpick.domain.admin.product.controller;

import co.kr.allpick.domain.admin.product.controller.docs.InquiryControllerDocs;
import co.kr.allpick.domain.admin.product.dto.InquiryAnswerRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryAnswerResponseDto;
import co.kr.allpick.domain.admin.product.dto.InquiryCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryResponseDto;
import co.kr.allpick.domain.admin.product.service.InquiryService;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import co.kr.allpick.global.config.JwtUserInfoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryController implements InquiryControllerDocs {

    private final InquiryService inquiryService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<InquiryResponseDto>> createInquiry(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody @Valid InquiryCreateRequestDto request) {
        return ApiResponse.success("문의가 등록되었습니다.", inquiryService.createInquiry(userInfo.getMemberId(), request));
    }

    @Override
    @GetMapping("/{inquiryId}")
    public ResponseEntity<ApiResponse<InquiryResponseDto>> getInquiryById(
            @PathVariable("inquiryId") Long inquiryId) {
        return ApiResponse.success("문의 조회 성공.", inquiryService.getInquiryById(inquiryId));
    }

    @Override
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<InquiryResponseDto>>> getMyInquiries(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        return ApiResponse.success("내 문의 목록 조회 성공.", inquiryService.getMyInquiries(userInfo.getMemberId()));
    }

    @Override
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<InquiryResponseDto>>> getAllInquiries() {
        return ApiResponse.success("전체 문의 목록 조회 성공.", inquiryService.getAllInquiries());
    }

    @Override
    @PostMapping("/{inquiryId}/answers/admin")
    public ResponseEntity<ApiResponse<InquiryAnswerResponseDto>> addAdminAnswer(
            @PathVariable("inquiryId") Long inquiryId,
            @RequestBody @Valid InquiryAnswerRequestDto request,
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo) {
        return ApiResponse.success("답변이 등록되었습니다.", inquiryService.addAdminAnswer(inquiryId, request, adminInfo.getAdminId()));
    }

    @Override
    @PostMapping("/{inquiryId}/answers/seller")
    public ResponseEntity<ApiResponse<InquiryAnswerResponseDto>> addSellerAnswer(
            @PathVariable("inquiryId") Long inquiryId,
            @RequestBody @Valid InquiryAnswerRequestDto request,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        return ApiResponse.success("답변이 등록되었습니다.", inquiryService.addSellerAnswer(inquiryId, request, userInfo.getMemberId()));
    }

    @Override
    @PatchMapping("/{inquiryId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelInquiry(
            @PathVariable("inquiryId") Long inquiryId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        inquiryService.cancelInquiry(inquiryId, userInfo.getMemberId());
        return ApiResponse.success("문의가 취소되었습니다.", null);
    }
}