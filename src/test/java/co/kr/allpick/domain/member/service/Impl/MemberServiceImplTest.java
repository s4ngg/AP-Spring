package co.kr.allpick.domain.member.service.Impl;

import co.kr.allpick.domain.coupon.entity.MemberCoupon;
import co.kr.allpick.domain.member.dto.MemberResponseDto;
import co.kr.allpick.domain.member.dto.mypage.MemberUpdateRequestDto;
import co.kr.allpick.domain.member.dto.mypage.PasswordChangeRequestDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.entity.MemberGrade;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.member.service.impl.MemberServiceImpl;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.domain.order.entity.Order;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Member createMember() {
        return Member.builder()
                .id(1L)
                .email("member@allpick.com")
                .password("encodedPassword")
                .name("test member")
                .phone("01012345678")
                .address("Seoul")
                .grade(MemberGrade.NORMAL)
                .build();
    }

    private Member createMemberWithPassword(String encodedPassword) {
        return Member.createLocal(
                "member@allpick.com",
                encodedPassword,
                "test member",
                "01012345678",
                "Seoul"
        );
    }

    @Nested
    @DisplayName("getMember")
    class GetMember {

        @Test
        @DisplayName("success")
        void getMember_success() {
            Member member = createMember();
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));

            MemberResponseDto result = memberService.getMember(1L);

            assertThat(result.getEmail()).isEqualTo("member@allpick.com");
            assertThat(result.getName()).isEqualTo("test member");
            assertThat(result.getPhone()).isEqualTo("01012345678");
            assertThat(result.getAddress()).isEqualTo("Seoul");
        }

        @Test
        @DisplayName("fail when member does not exist")
        void getMember_memberNotFound() {
            given(memberRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.getMember(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("updateMember")
    class UpdateMember {

        @Test
        @DisplayName("success")
        void updateMember_success() {
            Member member = createMember();
            MemberUpdateRequestDto request = new MemberUpdateRequestDto(
                    "updated member",
                    "01087654321",
                    "Busan"
            );
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));

            MemberResponseDto result = memberService.updateMember(1L, request);

            assertThat(result.getName()).isEqualTo("updated member");
            assertThat(result.getPhone()).isEqualTo("01087654321");
            assertThat(result.getAddress()).isEqualTo("Busan");
            assertThat(member.getName()).isEqualTo("updated member");
            assertThat(member.getPhone()).isEqualTo("01087654321");
            assertThat(member.getAddress()).isEqualTo("Busan");
        }

        @Test
        @DisplayName("fail when member does not exist")
        void updateMember_memberNotFound() {
            MemberUpdateRequestDto request = new MemberUpdateRequestDto(
                    "updated member",
                    "01087654321",
                    "Busan"
            );
            given(memberRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.updateMember(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePassword {

        @Test
        @DisplayName("success and updates encoded password")
        void changePassword_success_updatesEncodedPassword() {
            Member member = createMemberWithPassword("encodedOldPassword");
            PasswordChangeRequestDto request = new PasswordChangeRequestDto("OldPass1!", "NewPass1!");
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(passwordEncoder.matches(request.getCurrentPassword(), "encodedOldPassword")).willReturn(true);
            given(passwordEncoder.matches(request.getNewPassword(), "encodedOldPassword")).willReturn(false);
            given(passwordEncoder.encode(request.getNewPassword())).willReturn("encodedNewPassword");

            memberService.changePassword(1L, request);

            verify(passwordEncoder).encode(request.getNewPassword());
            assertThat(member.getPassword()).isEqualTo("encodedNewPassword");
        }

        @Test
        @DisplayName("fail when member does not exist")
        void changePassword_memberNotFound() {
            PasswordChangeRequestDto request = new PasswordChangeRequestDto("OldPass1!", "NewPass1!");
            given(memberRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.changePassword(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("fail when current password mismatches")
        void changePassword_currentPasswordMismatch() {
            Member member = createMemberWithPassword("encodedOldPassword");
            PasswordChangeRequestDto request = new PasswordChangeRequestDto("WrongPass1!", "NewPass1!");
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(passwordEncoder.matches(request.getCurrentPassword(), "encodedOldPassword")).willReturn(false);

            assertThatThrownBy(() -> memberService.changePassword(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CURRENT_PASSWORD_MISMATCH);
        }

        @Test
        @DisplayName("fail when new password is same as current password")
        void changePassword_samePassword() {
            Member member = createMemberWithPassword("encodedOldPassword");
            PasswordChangeRequestDto request = new PasswordChangeRequestDto("OldPass1!", "OldPass1!");
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(passwordEncoder.matches(request.getCurrentPassword(), "encodedOldPassword")).willReturn(true);
            given(passwordEncoder.matches(request.getNewPassword(), "encodedOldPassword")).willReturn(true);

            assertThatThrownBy(() -> memberService.changePassword(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SAME_PASSWORD);
        }

        @Test
        @DisplayName("current password mismatch stops before encode")
        void changePassword_currentMismatchStopsBeforeEncode() {
            Member member = createMemberWithPassword("encodedOldPassword");
            PasswordChangeRequestDto request = new PasswordChangeRequestDto("WrongPass1!", "NewPass1!");
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(passwordEncoder.matches(request.getCurrentPassword(), "encodedOldPassword")).willReturn(false);

            assertThatThrownBy(() -> memberService.changePassword(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CURRENT_PASSWORD_MISMATCH);
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Nested
    @DisplayName("deleteMember")
    class DeleteMember {

        @Test
        @DisplayName("success and changes status to zero")
        void deleteMember_success_setsStatusZero() {
            Member member = createMember();
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));

            memberService.deleteMember(1L);

            assertThat(member.getStatus()).isZero();
        }

        @Test
        @DisplayName("fail when member does not exist")
        void deleteMember_memberNotFound() {
            given(memberRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.deleteMember(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getMyOrders")
    class GetMyOrders {

        @Test
        @DisplayName("returns empty list when there are no orders")
        void getMyOrders_emptyList() {
            given(orderRepository.findByMemberIdOrderByOrderedAtDesc(1L)).willReturn(Collections.emptyList());

            List<OrderResponseDto> result = memberService.getMyOrders(1L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns mapped order dtos")
        void getMyOrders_returnsMappedDtos() {
            MemberCoupon memberCoupon = mock(MemberCoupon.class);
            given(memberCoupon.getMemberCouponId()).willReturn(10L);
            Order order = Order.builder()
                    .memberCoupon(memberCoupon)
                    .orderNumber("ORD-20260502-000001")
                    .totalAmount(BigDecimal.valueOf(30000))
                    .discountAmount(BigDecimal.valueOf(1000))
                    .shippingFee(3000)
                    .status(Order.OrderStatus.PAID)
                    .orderedAt(LocalDateTime.of(2026, 5, 2, 10, 0))
                    .build();
            given(orderRepository.findByMemberIdOrderByOrderedAtDesc(1L)).willReturn(List.of(order));

            List<OrderResponseDto> result = memberService.getMyOrders(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getOrderNumber()).isEqualTo("ORD-20260502-000001");
            assertThat(result.get(0).getMemberCouponId()).isEqualTo(10L);
            assertThat(result.get(0).getStatus()).isEqualTo(Order.OrderStatus.PAID);
            assertThat(result.get(0).getOrderItems()).isEmpty();
        }
    }
}
