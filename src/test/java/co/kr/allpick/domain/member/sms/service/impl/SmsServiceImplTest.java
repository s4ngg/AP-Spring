package co.kr.allpick.domain.member.sms.service.impl;

import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.global.config.CoolSmsProperties;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SmsServiceImplTest {

    @Mock DefaultMessageService messageService;
    @Mock MemberRepository memberRepository;
    @Mock RedisTemplate<String, String> redisTemplate;
    @Mock ValueOperations<String, String> valueOperations;
    @Mock PasswordEncoder passwordEncoder;
    @Mock CoolSmsProperties coolSmsProperties;

    @InjectMocks
    SmsServiceImpl smsService;

    // ==================== 인증번호 발송 ====================

    @Test
    @DisplayName("인증번호 발송 성공")
    void sendVerificationCode_validPhone_success() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(coolSmsProperties.getSender()).willReturn("01012345678");

        // when
        smsService.sendVerificationCode("01012345678");

        // then
        verify(valueOperations, times(1)).set(anyString(), anyString(), anyLong(), any());
        verify(messageService, times(1)).sendOne(any());
    }

    @Test
    @DisplayName("인증번호 발송 실패 - SMS 전송 오류")
    void sendVerificationCode_smsFailed_throwException() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(coolSmsProperties.getSender()).willReturn("01012345678");
        willThrow(new RuntimeException("SMS 오류")).given(messageService).sendOne(any());

        // when & then
        assertThatThrownBy(() -> smsService.sendVerificationCode("01012345678"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.SMS_SEND_FAILED.getMessage());
    }

    // ==================== 인증번호 검증 ====================

    @Test
    @DisplayName("인증번호 검증 성공")
    void verifyCode_validCode_success() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn("encodedCode");
        given(passwordEncoder.matches("123456", "encodedCode")).willReturn(true);

        // when
        smsService.verifyCode("01012345678", "123456");

        // then
        verify(redisTemplate, times(1)).delete(anyString());
        verify(valueOperations, times(1)).set(anyString(), eq("true"), anyLong(), any());
    }

    @Test
    @DisplayName("인증번호 검증 실패 - 코드 없음")
    void verifyCode_codeNotFound_throwException() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> smsService.verifyCode("01012345678", "123456"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_VERIFICATION_CODE.getMessage());
    }

    @Test
    @DisplayName("인증번호 검증 실패 - 코드 불일치")
    void verifyCode_invalidCode_throwException() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn("encodedCode");
        given(passwordEncoder.matches("wrongCode", "encodedCode")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> smsService.verifyCode("01012345678", "wrongCode"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_VERIFICATION_CODE.getMessage());
    }

    // ==================== 아이디 찾기 ====================

    @Test
    @DisplayName("아이디 찾기 성공")
    void verifyAndFindId_validCode_returnEmail() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn("encodedCode");
        given(passwordEncoder.matches("123456", "encodedCode")).willReturn(true);
        given(memberRepository.findEmailByUserPhone("01012345678")).willReturn(Optional.of("test@test.com"));

        // when
        String email = smsService.verifyAndFindId("01012345678", "123456");

        // then
        assertThat(email).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("아이디 찾기 실패 - 존재하지 않는 회원")
    void verifyAndFindId_memberNotFound_throwException() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn("encodedCode");
        given(passwordEncoder.matches("123456", "encodedCode")).willReturn(true);
        given(memberRepository.findEmailByUserPhone("01012345678")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> smsService.verifyAndFindId("01012345678", "123456"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_FOUND_BY_PHONE.getMessage());
    }
}