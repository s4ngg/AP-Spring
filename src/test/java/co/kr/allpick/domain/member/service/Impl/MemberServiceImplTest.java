package co.kr.allpick.domain.member.service.Impl;

import co.kr.allpick.domain.member.dto.MemberResponseDto;
import co.kr.allpick.domain.member.dto.mypage.MemberUpdateRequestDto;
import co.kr.allpick.domain.member.dto.mypage.PasswordChangeRequestDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.entity.MemberGrade;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.member.service.impl.MemberServiceImpl;
import co.kr.allpick.domain.order.repository.OrderRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;

    // ─── 픽스처 ───

    private Member createMember() {
        return Member.builder()
                .email("test@test.com")
                .name("김상우")
                .phone("01012345678")
                .address("인천광역시 미추홀구")
                .grade(MemberGrade.NORMAL)
                .build();
    }

    private Member createMemberWithPassword(String encodedPassword) {
        return Member.createLocal("test@test.com", encodedPassword, "김상우", "01012345678", "인천광역시 미추홀구");
    }

    // ─────────────────────────────────────────
    // getMember
    // ─────────────────────────────────────────

    @Test
    @DisplayName("회원 정보 조회 성공")
    void 회원_정보_조회_성공() {
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(createMember()));

        MemberResponseDto result = memberService.getMember(memberId);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getName()).isEqualTo("김상우");
        assertThat(result.getGrade()).isEqualTo(MemberGrade.NORMAL);
    }

    @Test
    @DisplayName("회원 정보 조회 실패 - 존재하지 않는 회원")
    void 회원_정보_조회_실패_존재하지않는회원() {
        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMember(999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    // ─────────────────────────────────────────
    // updateMember
    // ─────────────────────────────────────────

    @Nested
    @DisplayName("회원 정보 수정")
    class UpdateMember {

        @Test
        @DisplayName("회원 정보 수정 성공")
        void 회원_정보_수정_성공() {
            Long memberId = 1L;
            MemberUpdateRequestDto request = new MemberUpdateRequestDto("이영훈", "01098765432", "서울시 강남구");
            given(memberRepository.findById(memberId)).willReturn(Optional.of(createMember()));

            MemberResponseDto result = memberService.updateMember(memberId, request);

            assertThat(result.getName()).isEqualTo("이영훈");
            assertThat(result.getPhone()).isEqualTo("01098765432");
            assertThat(result.getAddress()).isEqualTo("서울시 강남구");
        }

        @Test
        @DisplayName("회원 정보 수정 실패 - 존재하지 않는 회원")
        void 회원_정보_수정_실패_존재하지않는회원() {
            MemberUpdateRequestDto request = new MemberUpdateRequestDto("이름", "01012345678", "주소");
            given(memberRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.updateMember(999L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    // ─────────────────────────────────────────
    // changePassword
    // ─────────────────────────────────────────

    @Nested
    @DisplayName("비밀번호 변경")
    class ChangePassword {

        @Test
        @DisplayName("비밀번호 변경 성공 - encode() 호출 검증")
        void 비밀번호_변경_성공() {
            Long memberId = 1L;
            Member member = createMemberWithPassword("encodedOldPassword");
            PasswordChangeRequestDto request = new PasswordChangeRequestDto("OldPass1!", "NewPass1!");

            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(passwordEncoder.matches("OldPass1!", "encodedOldPassword")).willReturn(true);
            given(passwordEncoder.matches("NewPass1!", "encodedOldPassword")).willReturn(false);
            given(passwordEncoder.encode("NewPass1!")).willReturn("encodedNewPassword");

            memberService.changePassword(memberId, request);

            verify(passwordEncoder).encode("NewPass1!");
            assertThat(member.getPassword()).isEqualTo("encodedNewPassword");
        }

        @Test
        @DisplayName("비밀번호 변경 실패 - 회원 없음")
        void 비밀번호_변경_실패_회원없음() {
            PasswordChangeRequestDto request = new PasswordChangeRequestDto("OldPass1!", "NewPass1!");
            given(memberRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.changePassword(999L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
        void 비밀번호_변경_실패_현재비밀번호_불일치() {
            Long memberId = 1L;
            Member member = createMemberWithPassword("encodedOldPassword");
            PasswordChangeRequestDto request = new PasswordChangeRequestDto("WrongPass1!", "NewPass1!");

            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(passwordEncoder.matches("WrongPass1!", "encodedOldPassword")).willReturn(false);

            assertThatThrownBy(() -> memberService.changePassword(memberId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CURRENT_PASSWORD_MISMATCH);
        }

        @Test
        @DisplayName("비밀번호 변경 실패 - 새 비밀번호가 기존과 동일")
        void 비밀번호_변경_실패_새비밀번호_기존과_동일() {
            Long memberId = 1L;
            Member member = createMemberWithPassword("encodedOldPassword");
            PasswordChangeRequestDto request = new PasswordChangeRequestDto("OldPass1!", "OldPass1!");

            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(passwordEncoder.matches("OldPass1!", "encodedOldPassword")).willReturn(true);

            assertThatThrownBy(() -> memberService.changePassword(memberId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SAME_PASSWORD);
        }

        @Test
        @DisplayName("현재 비밀번호 틀리면 동일 비밀번호 검증 전에 예외 발생")
        void 현재비밀번호_틀리면_동일비밀번호_검증전_예외() {
            Long memberId = 1L;
            Member member = createMemberWithPassword("encodedOldPassword");
            PasswordChangeRequestDto request = new PasswordChangeRequestDto("WrongPass1!", "WrongPass1!");

            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(passwordEncoder.matches("WrongPass1!", "encodedOldPassword")).willReturn(false);

            assertThatThrownBy(() -> memberService.changePassword(memberId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CURRENT_PASSWORD_MISMATCH);

            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    // ─────────────────────────────────────────
    // deleteMember
    // ─────────────────────────────────────────

    @Nested
    @DisplayName("회원 탈퇴")
    class DeleteMember {

        @Test
        @DisplayName("회원 탈퇴 성공 - status 0 확인")
        void 회원_탈퇴_성공() {
            Long memberId = 1L;
            Member member = createMember();
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

            memberService.deleteMember(memberId);

            assertThat(member.getStatus()).isEqualTo(0);
        }

        @Test
        @DisplayName("회원 탈퇴 실패 - 존재하지 않는 회원")
        void 회원_탈퇴_실패_존재하지않는회원() {
            given(memberRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.deleteMember(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    // ─────────────────────────────────────────
    // getMyOrders
    // ─────────────────────────────────────────

    @Nested
    @DisplayName("내 주문 목록 조회")
    class GetMyOrders {

        @Test
        @DisplayName("주문 없을 때 빈 리스트 반환")
        void 주문_목록_조회_주문없으면_빈리스트_반환() {
            Long memberId = 1L;
            given(orderRepository.findByMemberIdOrderByOrderedAtDesc(memberId))
                    .willReturn(Collections.emptyList());

            List<?> result = memberService.getMyOrders(memberId);

            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        // 주문 있을 때 케이스는 OrderResponseDto.from()이 order.getMemberCoupon().getMemberCouponId()를
        // 호출하므로 memberCoupon이 null이면 NPE 발생 — 실제 버그 후보, 별도 이슈로 추적 필요
    }
}
