package co.kr.allpick.domain.member.service.Impl;

import co.kr.allpick.domain.member.dto.MemberResponseDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.entity.MemberGrade;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.member.service.impl.MemberServiceImpl;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberServiceImpl memberService;

    @Test
    @DisplayName("회원 정보 조회 성공")
    void 회원_정보_조회_성공() {
        // given
        Long memberId = 1L;
        Member mockMember = Member.builder()
                .email("test@test.com")
                .name("김상우")
                .phone("01012345678")
                .address("인천광역시 미추홀구")
                .grade(MemberGrade.NORMAL)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // when
        MemberResponseDto result = memberService.getMember(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getName()).isEqualTo("김상우");
        assertThat(result.getGrade()).isEqualTo(MemberGrade.NORMAL);
    }

    @Test
    @DisplayName("회원 정보 조회 실패 - 존재하지 않는 회원")
    void 회원_정보_조회_실패_존재하지않는회원() {
        // given
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMember(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }
}