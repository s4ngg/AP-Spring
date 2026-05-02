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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    MemberServiceImpl memberService;

    // ───────────────────────────────────────────────
    // getMember
    // ───────────────────────────────────────────────
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

    // ───────────────────────────────────────────────
    // updateMember
    // ───────────────────────────────────────────────
    @Nested
    @DisplayName("updateMember")
    class UpdateMember {

        @Test
        @DisplayName("회원 정보 수정 성공 - 수정된 MemberResponseDto 반환")
        void 회원_정보_수정_성공() {
            // given
            Long memberId = 1L;
            Member member = Member.builder()
                    .email("test@test.com")
                    .name("김상우")
                    .phone("01012345678")
                    .address("인천광역시 미추홀구")
                    .grade(MemberGrade.NORMAL)
                    .build();

            MemberUpdateRequestDto request = new MemberUpdateRequestDto();
            ReflectionTestUtils.setField(request, "name", "이영훈");
            ReflectionTestUtils.setField(request, "phone", "01098765432");
            ReflectionTestUtils.setField(request, "address", "서울시 강남구");

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            // when
            MemberResponseDto result = memberService.updateMember(memberId, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("이영훈");
            assertThat(result.getPhone()).isEqualTo("01098765432");
            assertThat(result.getAddress()).isEqualTo("서울시 강남구");
        }

        @Test
        @DisplayName("회원 정보 수정 실패 - 존재하지 않는 회원")
        void 회원_정보_수정_실패_존재하지않는회원() {
            // given
            Long memberId = 999L;
            MemberUpdateRequestDto request = new MemberUpdateRequestDto();
            ReflectionTestUtils.setField(request, "name", "이름");
            ReflectionTestUtils.setField(request, "phone", "01012345678");
            ReflectionTestUtils.setField(request, "address", "주소");

            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.updateMember(memberId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    // ───────────────────────────────────────────────
    // changePassword
    // ───────────────────────────────────────────────
    @Nested
    @DisplayName("changePassword")
    class ChangePassword {

        @Test
        @DisplayName("비밀번호 변경 성공 - passwordEncoder.encode() 호출 검증")
        void 비밀번호_변경_성공() {
            // given
            Long memberId = 1L;
            Member member = Member.builder()
                    .email("test@test.com")
                    .name("김상우")
                    .phone("01012345678")
                    .address("인천광역시 미추홀구")
                    .build();
            ReflectionTestUtils.setField(member, "password", "encodedOldPassword");

            PasswordChangeRequestDto request = new PasswordChangeRequestDto();
            ReflectionTestUtils.setField(request, "currentPassword", "OldPass1!");
            ReflectionTestUtils.setField(request, "newPassword", "NewPass1!");

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("OldPass1!", "encodedOldPassword")).thenReturn(true);
            when(passwordEncoder.matches("NewPass1!", "encodedOldPassword")).thenReturn(false);
            when(passwordEncoder.encode("NewPass1!")).thenReturn("encodedNewPassword");

            // when
            memberService.changePassword(memberId, request);

            // then
            verify(passwordEncoder).encode("NewPass1!");
        }

        @Test
        @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
        void 비밀번호_변경_실패_현재비밀번호_불일치() {
            // given
            Long memberId = 1L;
            Member member = Member.builder()
                    .email("test@test.com")
                    .name("김상우")
                    .phone("01012345678")
                    .address("인천광역시 미추홀구")
                    .build();
            ReflectionTestUtils.setField(member, "password", "encodedOldPassword");

            PasswordChangeRequestDto request = new PasswordChangeRequestDto();
            ReflectionTestUtils.setField(request, "currentPassword", "WrongPass1!");
            ReflectionTestUtils.setField(request, "newPassword", "NewPass1!");

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("WrongPass1!", "encodedOldPassword")).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> memberService.changePassword(memberId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CURRENT_PASSWORD_MISMATCH);
        }

        @Test
        @DisplayName("비밀번호 변경 실패 - 새 비밀번호가 기존과 동일")
        void 비밀번호_변경_실패_새비밀번호_기존과_동일() {
            // given
            Long memberId = 1L;
            Member member = Member.builder()
                    .email("test@test.com")
                    .name("김상우")
                    .phone("01012345678")
                    .address("인천광역시 미추홀구")
                    .build();
            ReflectionTestUtils.setField(member, "password", "encodedOldPassword");

            PasswordChangeRequestDto request = new PasswordChangeRequestDto();
            ReflectionTestUtils.setField(request, "currentPassword", "OldPass1!");
            ReflectionTestUtils.setField(request, "newPassword", "OldPass1!");

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("OldPass1!", "encodedOldPassword")).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.changePassword(memberId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SAME_PASSWORD);
        }

        @Test
        @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 틀리면 동일 비밀번호 검증 전에 예외 발생")
        void 비밀번호_변경_실패_현재비밀번호_틀리면_동일비밀번호_검증전_예외() {
            // given
            Long memberId = 1L;
            Member member = Member.builder()
                    .email("test@test.com")
                    .name("김상우")
                    .phone("01012345678")
                    .address("인천광역시 미추홀구")
                    .build();
            ReflectionTestUtils.setField(member, "password", "encodedOldPassword");

            PasswordChangeRequestDto request = new PasswordChangeRequestDto();
            ReflectionTestUtils.setField(request, "currentPassword", "WrongPass1!");
            ReflectionTestUtils.setField(request, "newPassword", "WrongPass1!");

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("WrongPass1!", "encodedOldPassword")).thenReturn(false);

            // when & then
            // CURRENT_PASSWORD_MISMATCH 예외 발생 후 SAME_PASSWORD 검증은 도달하지 않음
            assertThatThrownBy(() -> memberService.changePassword(memberId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CURRENT_PASSWORD_MISMATCH);

            // passwordEncoder.matches()는 currentPassword 검증 시 1회만 호출됨
            verify(passwordEncoder).matches("WrongPass1!", "encodedOldPassword");
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    // ───────────────────────────────────────────────
    // deleteMember
    // ───────────────────────────────────────────────
    @Nested
    @DisplayName("deleteMember")
    class DeleteMember {

        @Test
        @DisplayName("회원 탈퇴 성공 - member.delete() 호출 검증")
        void 회원_탈퇴_성공() {
            // given
            Long memberId = 1L;
            Member member = Member.builder()
                    .email("test@test.com")
                    .name("김상우")
                    .phone("01012345678")
                    .address("인천광역시 미추홀구")
                    .build();

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            // when
            memberService.deleteMember(memberId);

            // then: member.delete() 호출 시 status가 0으로 변경됨
            assertThat(member.getStatus()).isEqualTo(0);
        }

        @Test
        @DisplayName("회원 탈퇴 실패 - 존재하지 않는 회원")
        void 회원_탈퇴_실패_존재하지않는회원() {
            // given
            Long memberId = 999L;
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.deleteMember(memberId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    // ───────────────────────────────────────────────
    // getMyOrders
    // ───────────────────────────────────────────────
    @Nested
    @DisplayName("getMyOrders")
    class GetMyOrders {

        @Test
        @DisplayName("주문 목록 조회 - 주문 없을 때 빈 리스트 반환")
        void 주문_목록_조회_주문없으면_빈리스트_반환() {
            // given
            Long memberId = 1L;
            when(orderRepository.findByMemberIdOrderByOrderedAtDesc(memberId))
                    .thenReturn(Collections.emptyList());

            // when
            List<?> result = memberService.getMyOrders(memberId);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }
}