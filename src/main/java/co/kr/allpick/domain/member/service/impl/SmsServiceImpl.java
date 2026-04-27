package co.kr.allpick.domain.member.service.impl;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.member.service.SmsService;
import co.kr.allpick.global.config.CoolSmsProperties;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LogManager.getLogger(SmsServiceImpl.class);

    private final DefaultMessageService messageService;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final CoolSmsProperties coolSmsProperties;

    private static final String CODE_PREFIX = "sms:code:";
    private static final String VERIFIED_PREFIX = "sms:verified:";
    private static final long CODE_TTL = 5;
    private static final long VERIFIED_TTL = 30;

    @Override
    public void sendVerificationCode(String phoneNumber) {
        logger.info("[SMS] 인증번호 발송 요청 - {}", phoneNumber);
        String code = generateCode();

        String encodedCode = passwordEncoder.encode(code);
        redisTemplate.opsForValue().set(CODE_PREFIX + phoneNumber, encodedCode, CODE_TTL, TimeUnit.MINUTES);

        Message message = new Message();
        message.setFrom(coolSmsProperties.getSender());
        message.setTo(phoneNumber);
        message.setText("[AllPick] 인증번호: " + code + "\n5분 내 입력해주세요.");

        try {
            messageService.sendOne(new SingleMessageSendingRequest(message));
            logger.info("[SMS] 인증번호 발송 성공 - {}", phoneNumber);
        } catch (Exception e) {
            logger.error("[SMS] 인증번호 발송 실패 - {}", phoneNumber, e);
            throw new BusinessException(ErrorCode.SMS_SEND_FAILED);
        }
    }

    @Override
    public String verifyAndFindId(String phoneNumber, String inputCode) {
        logger.info("[SMS] 아이디 찾기 인증 요청 - {}", phoneNumber); //
        String encodedCode = redisTemplate.opsForValue().get(CODE_PREFIX + phoneNumber);

        if (encodedCode == null || !passwordEncoder.matches(inputCode, encodedCode)) {
            logger.warn("[SMS] 인증번호 불일치 - {}", phoneNumber); 
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        redisTemplate.delete(CODE_PREFIX + phoneNumber);
        return memberRepository.findEmailByUserPhone(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 번호로 가입된 계정이 없습니다."));
    }

    @Override
    public void verifyCode(String phoneNumber, String inputCode) {
        logger.info("[SMS] 인증번호 확인 요청 - {}", phoneNumber); 
        String encodedCode = redisTemplate.opsForValue().get(CODE_PREFIX + phoneNumber);

        if (encodedCode == null || !passwordEncoder.matches(inputCode, encodedCode)) {
            logger.warn("[SMS] 인증번호 불일치 - {}", phoneNumber); 
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        redisTemplate.delete(CODE_PREFIX + phoneNumber);
        redisTemplate.opsForValue().set(VERIFIED_PREFIX + phoneNumber, "true", VERIFIED_TTL, TimeUnit.MINUTES);
        logger.info("[SMS] 인증 완료 - {}", phoneNumber); 
    }

    @Override
    public boolean isVerified(String phoneNumber) {
        return Boolean.TRUE.toString().equals(redisTemplate.opsForValue().get(VERIFIED_PREFIX + phoneNumber));
    }

    @Override
    public void removeVerified(String phoneNumber) {
        logger.info("[SMS] 인증 정보 삭제 - {}", phoneNumber); 
        redisTemplate.delete(VERIFIED_PREFIX + phoneNumber);
    }

    private String generateCode() {
        return String.valueOf(100000 + secureRandom.nextInt(900000));
    }
}