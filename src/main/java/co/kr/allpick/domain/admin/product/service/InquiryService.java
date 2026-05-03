package co.kr.allpick.domain.admin.product.service;

import co.kr.allpick.domain.admin.product.dto.InquiryResponseDto;
import co.kr.allpick.domain.admin.product.dto.InquiryCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryAnswerRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryAnswerResponseDto;
import java.util.List;

public interface InquiryService {

    // 문의 등록
    InquiryResponseDto createInquiry(Long memberId, InquiryCreateRequestDto request);

    // 문의 상세 조회
    InquiryResponseDto getInquiryById(Long inquiryId);

    // 내 문의 목록 조회
    List<InquiryResponseDto> getMyInquiries(Long memberId);

    // 전체 문의 목록 조회 (관리자)
    List<InquiryResponseDto> getAllInquiries();

    // 관리자 답변 등록
    InquiryAnswerResponseDto addAdminAnswer(Long inquiryId, InquiryAnswerRequestDto request, Long adminId);

    // 판매자 답변 등록
    InquiryAnswerResponseDto addSellerAnswer(Long inquiryId, InquiryAnswerRequestDto request, Long memberId);

    // 문의 취소
    void cancelInquiry(Long inquiryId, Long memberId);

}
