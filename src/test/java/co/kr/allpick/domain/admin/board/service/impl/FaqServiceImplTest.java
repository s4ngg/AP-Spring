package co.kr.allpick.domain.admin.board.service.impl;

import co.kr.allpick.domain.admin.board.dto.FaqCreateRequestDto;
import co.kr.allpick.domain.admin.board.dto.FaqResponseDto;
import co.kr.allpick.domain.admin.board.entity.Faq;
import co.kr.allpick.domain.admin.board.repository.FaqRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class FaqServiceImplTest {

    @InjectMocks
    private FaqServiceImpl faqService;

    @Mock
    private FaqRepository faqRepository;

    // ─── 픽스처 ───

    private FaqCreateRequestDto createDto() {
        return new FaqCreateRequestDto(
                Faq.FaqCategory.DELIVERY,
                "배송은 얼마나 걸리나요?",
                "평균 2~3일 소요됩니다.",
                1
        );
    }

    private Faq createFaq() {
        return Faq.builder()
                .adminId(1L)
                .category(Faq.FaqCategory.DELIVERY)
                .title("배송은 얼마나 걸리나요?")
                .content("평균 2~3일 소요됩니다.")
                .displayOrder(1)
                .build();
    }

    // ==================== createFaq ====================

    @Test
    @DisplayName("FAQ 등록 성공")
    void createFaq_success() {
        // given
        Long adminId = 1L;
        FaqCreateRequestDto dto = createDto();

        // when
        FaqResponseDto result = faqService.createFaq(adminId, dto);

        // then
        then(faqRepository).should().save(any(Faq.class));
        assertThat(result.getTitle()).isEqualTo(dto.getTitle());
    }

    // ==================== getAllFaqs ====================

    @Test
    @DisplayName("FAQ 전체 조회 성공")
    void getAllFaqs_success() {
        // given
        given(faqRepository.findAllByDeletedAtIsNullOrderByDisplayOrderAsc())
                .willReturn(List.of(createFaq()));

        // when
        List<FaqResponseDto> result = faqService.getAllFaqs();

        // then
        assertThat(result).hasSize(1);
    }

    // ==================== getFaqsByCategory ====================

    @Test
    @DisplayName("카테고리별 FAQ 조회 성공")
    void getFaqsByCategory_success() {
        // given
        given(faqRepository.findAllByCategoryAndDeletedAtIsNullOrderByDisplayOrderAsc(Faq.FaqCategory.DELIVERY))
                .willReturn(List.of(createFaq()));

        // when
        List<FaqResponseDto> result = faqService.getFaqsByCategory(Faq.FaqCategory.DELIVERY);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(Faq.FaqCategory.DELIVERY);
    }

    // ==================== updateFaq ====================

    @Test
    @DisplayName("FAQ 수정 성공")
    void updateFaq_success() {
        // given
        Long faqId = 1L;
        FaqCreateRequestDto dto = createDto();
        given(faqRepository.findById(faqId)).willReturn(Optional.of(createFaq()));

        // when
        FaqResponseDto result = faqService.updateFaq(faqId, dto);

        // then
        assertThat(result.getTitle()).isEqualTo(dto.getTitle());
    }

    @Test
    @DisplayName("FAQ 수정 실패 - 존재하지 않는 FAQ")
    void updateFaq_notFound() {
        // given
        given(faqRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> faqService.updateFaq(999L, createDto()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAQ_NOT_FOUND);
    }

    // ==================== toggleVisibility ====================

    @Test
    @DisplayName("FAQ 노출 여부 변경 성공")
    void toggleVisibility_success() {
        // given
        Long faqId = 1L;
        given(faqRepository.findById(faqId)).willReturn(Optional.of(createFaq()));

        // when
        faqService.toggleVisibility(faqId, false);

        // then - 예외 없이 정상 실행 확인
        then(faqRepository).should().findById(faqId);
    }

    @Test
    @DisplayName("FAQ 노출 여부 변경 실패 - 존재하지 않는 FAQ")
    void toggleVisibility_notFound() {
        // given
        given(faqRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> faqService.toggleVisibility(999L, false))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAQ_NOT_FOUND);
    }

    // ==================== deleteFaq ====================

    @Test
    @DisplayName("FAQ 삭제 성공")
    void deleteFaq_success() {
        // given
        Long faqId = 1L;
        Faq faq = createFaq();
        given(faqRepository.findById(faqId)).willReturn(Optional.of(faq));

        // when
        faqService.deleteFaq(faqId);

        // then
        assertThat(faq.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("FAQ 삭제 실패 - 존재하지 않는 FAQ")
    void deleteFaq_notFound() {
        // given
        given(faqRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> faqService.deleteFaq(999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAQ_NOT_FOUND);
    }
}


