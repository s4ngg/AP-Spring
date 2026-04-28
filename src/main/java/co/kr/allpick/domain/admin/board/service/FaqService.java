package co.kr.allpick.domain.admin.board.service;

import co.kr.allpick.domain.admin.board.dto.FaqCreateRequestDto;
import co.kr.allpick.domain.admin.board.dto.FaqResponseDto;
import co.kr.allpick.domain.admin.board.entity.Faq;

import java.util.List;

public interface FaqService {

    // FAQ 등록
    FaqResponseDto createFaq(Long adminId, FaqCreateRequestDto request);

    // FAQ 전체 조회
    List<FaqResponseDto> getAllFaqs();

    // FAQ 카테고리별 조회
    List<FaqResponseDto> getFaqsByCategory(Faq.FaqCategory category);

    // FAQ 수정
    FaqResponseDto updateFaq(Long faqId, FaqCreateRequestDto request);

    // FAQ 노출 여부 변경
    void toggleVisibility(Long faqId, boolean isVisible);

    // FAQ 삭제 (소프트 딜리트)
    void deleteFaq(Long faqId);
}