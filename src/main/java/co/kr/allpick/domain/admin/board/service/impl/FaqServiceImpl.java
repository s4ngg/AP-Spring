package co.kr.allpick.domain.admin.board.service.impl;

import co.kr.allpick.domain.admin.board.dto.FaqCreateRequestDto;
import co.kr.allpick.domain.admin.board.dto.FaqResponseDto;
import co.kr.allpick.domain.admin.board.entity.Faq;
import co.kr.allpick.domain.admin.board.repository.FaqRepository;
import co.kr.allpick.domain.admin.board.service.FaqService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqServiceImpl implements FaqService {

    private final FaqRepository faqRepository;
    private static final Logger logger = LogManager.getLogger(FaqServiceImpl.class);

    @Override
    @Transactional
    public FaqResponseDto createFaq(Long adminId, FaqCreateRequestDto request) {
        Faq faq = request.toEntity(adminId);
        faqRepository.save(faq);
        logger.info("[FaqServiceImpl] FAQ 등록 완료 - adminId: {}", adminId);
        return FaqResponseDto.from(faq);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaqResponseDto> getAllFaqs() {
        return faqRepository.findAllByDeletedAtIsNullOrderByDisplayOrderAsc()
                .stream()
                .map(FaqResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaqResponseDto> getFaqsByCategory(Faq.FaqCategory category) {
        return faqRepository.findAllByCategoryAndDeletedAtIsNullOrderByDisplayOrderAsc(category)
                .stream()
                .map(FaqResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FaqResponseDto updateFaq(Long faqId, FaqCreateRequestDto request) {
        Faq faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FAQ_NOT_FOUND));
        faq.update(request.getCategory(), request.getTitle(), request.getContent(), request.getDisplayOrder());
        logger.info("[FaqServiceImpl] FAQ 수정 완료 - faqId: {}", faqId);
        return FaqResponseDto.from(faq);
    }

    @Override
    @Transactional
    public void toggleVisibility(Long faqId, boolean isVisible) {
        Faq faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FAQ_NOT_FOUND));
        faq.toggleVisibility(isVisible);
        logger.info("[FaqServiceImpl] FAQ 노출 여부 변경 - faqId: {}, isVisible: {}", faqId, isVisible);
    }

    @Override
    @Transactional
    public void deleteFaq(Long faqId) {
        Faq faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FAQ_NOT_FOUND));
        faq.delete();
        logger.info("[FaqServiceImpl] FAQ 삭제 완료 - faqId: {}", faqId);
    }
}