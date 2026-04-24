package co.kr.allpick.domain.member.service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final DefaultMessageService messageService;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    private static final String CODE_PREFIX = "sms:code:";
    private static final String VERIFIED_PREFIX = "sms:verified:";
    private static final long CODE_TTL = 5;       // 인증번호 5분
    private static final long VERIFIED_TTL = 10;  // 인증 완료 10분

    @Value("${coolsms.sender}")
    private String sender;

    @Override
    public void sendVerificationCode(String phoneNumber) {
        String code = generateCode();

        // BCrypt로 암호화해서 Redis에 저장 (5분 TTL)
        String encodedCode = passwordEncoder.encode(code);
        redisTemplate.opsForValue().set(CODE_PREFIX + phoneNumber, encodedCode, CODE_TTL, TimeUnit.MINUTES);

        Message message = new Message();
        message.setFrom(sender);
        message.setTo(phoneNumber);
        message.setText("[AllPick] 인증번호: " + code + "\n5분 내 입력해주세요.");

        try {
            messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SMS_SEND_FAILED);
        }
    }

    @Override
    public String verifyAndFindId(String phoneNumber, String inputCode) {
        String encodedCode = redisTemplate.opsForValue().get(CODE_PREFIX + phoneNumber);

        if (encodedCode == null || !passwordEncoder.matches(inputCode, encodedCode)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        redisTemplate.delete(CODE_PREFIX + phoneNumber);

        return memberRepository.findEmailByUserPhone(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 번호로 가입된 계정이 없습니다."));
    }

    @Override
    public void verifyCode(String phoneNumber, String inputCode) {
        String encodedCode = redisTemplate.opsForValue().get(CODE_PREFIX + phoneNumber);

        if (encodedCode == null || !passwordEncoder.matches(inputCode, encodedCode)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        redisTemplate.delete(CODE_PREFIX + phoneNumber);

        // 인증 완료 표시 Redis에 저장 (10분 TTL)
        redisTemplate.opsForValue().set(VERIFIED_PREFIX + phoneNumber, "true", VERIFIED_TTL, TimeUnit.MINUTES);
    }

    @Override
    public boolean isVerified(String phoneNumber) {
        return Boolean.TRUE.toString().equals(redisTemplate.opsForValue().get(VERIFIED_PREFIX + phoneNumber));
    }

    @Override
    public void removeVerified(String phoneNumber) {
        redisTemplate.delete(VERIFIED_PREFIX + phoneNumber);
    }

    private String generateCode() {
        return String.valueOf(100000 + secureRandom.nextInt(900000));
    }
}