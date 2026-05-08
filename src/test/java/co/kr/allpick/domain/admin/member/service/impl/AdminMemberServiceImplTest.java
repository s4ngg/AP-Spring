package co.kr.allpick.domain.admin.member.service.impl;

import co.kr.allpick.domain.admin.member.dto.MemberListResponseDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AdminMemberServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    AdminMemberServiceImpl adminMemberService;

    @Test
    @DisplayName("구매자 목록 조회 성공")
    void 구매자_목록_조회_성공() {
        // given
        Member m1 = member(1L, 1);
        Member m2 = member(2L, 0);
        given(memberRepository.findAllByOrderByCreatedAtDesc()).willReturn(List.of(m1, m2));

        // when
        List<MemberListResponseDto> result = adminMemberService.getMembers(null);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getStatus()).isEqualTo(0);
    }

    @Test
    @DisplayName("구매자 정지 성공 - 활성(1) → 정지(0)")
    void 구매자_정지_성공() {
        // given
        Member member = member(1L, 1);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        // when
        adminMemberService.toggleMemberStatus(null, 1L);

        // then
        assertThat(member.getStatus()).isEqualTo(0);
    }

    @Test
    @DisplayName("구매자 활성화 성공 - 정지(0) → 활성(1)")
    void 구매자_활성화_성공() {
        // given
        Member member = member(1L, 0);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        // when
        adminMemberService.toggleMemberStatus(null, 1L);

        // then
        assertThat(member.getStatus()).isEqualTo(1);
    }

    @Test
    @DisplayName("상태변경 실패 - 회원 없음")
    void 상태변경_실패_회원없음() {
        // given
        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminMemberService.toggleMemberStatus(null, 999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    private Member member(Long id, int status) {
        return Member.builder()
                .email("test" + id + "@example.com")
                .name("테스터" + id)
                .phone("010-0000-000" + id)
                .address("서울시")
                .status(status)
                .build();
    }
}
