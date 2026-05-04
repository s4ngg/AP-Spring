package co.kr.allpick.domain.terms.service.impl;

import co.kr.allpick.domain.member.entity.Terms;
import co.kr.allpick.domain.member.repository.TermsRepository;
import co.kr.allpick.domain.member.service.impl.TermsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TermsServiceImplTest {

    @Mock
    TermsRepository termsRepository;

    @InjectMocks
    TermsServiceImpl termsService;

    @Test
    @DisplayName("활성 약관 목록 조회 성공")
    void getActiveTerms_success() {
        // given
        Terms terms1 = Terms.builder().isActive(true).build();
        Terms terms2 = Terms.builder().isActive(true).build();

        given(termsRepository.findByIsActiveTrue()).willReturn(List.of(terms1, terms2));

        // when
        List<Terms> result = termsService.getActiveTerms();

        // then
        assertThat(result).hasSize(2);
        verify(termsRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    @DisplayName("활성 약관 없을 경우 빈 리스트 반환")
    void getActiveTerms_empty() {
        // given
        given(termsRepository.findByIsActiveTrue()).willReturn(List.of());

        // when
        List<Terms> result = termsService.getActiveTerms();

        // then
        assertThat(result).isEmpty();
        verify(termsRepository, times(1)).findByIsActiveTrue();
    }
}