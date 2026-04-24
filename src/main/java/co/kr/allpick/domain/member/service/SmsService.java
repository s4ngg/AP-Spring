package co.kr.allpick.domain.member.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final DefaultMessageService messageService;
    private final MemberRepository memberRepository;

    private final Map<String, String> verificationStore = new ConcurrentHashMap<>();
    private final Set<String> verifiedPhones = ConcurrentHashMap.newKeySet(); // ← 추가

    @Value("${coolsms.sender}")
    private String sender;

    // 인증번호 발송
    public void sendVerificationCode(String phoneNumber) {
        String code = generateCode();
        verificationStore.put(phoneNumber, code);

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

    // 인증번호 확인 후 ID(email) 반환
 // 인증번호 확인 후 ID(email) 반환
    public String verifyAndFindId(String phoneNumber, String inputCode) {
        String storedCode = verificationStore.get(phoneNumber);

        if (storedCode == null || !storedCode.equals(inputCode)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        verificationStore.remove(phoneNumber);

        return memberRepository.findEmailByUserPhone(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 번호로 가입된 계정이 없습니다."));
    }

    // verifyCode 기존 메서드 교체
    public void verifyCode(String phoneNumber, String inputCode) {
        String storedCode = verificationStore.get(phoneNumber);
        if (storedCode == null || !storedCode.equals(inputCode)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        verificationStore.remove(phoneNumber);
        verifiedPhones.add(phoneNumber); // ← 추가
    }

    // 아래 두 메서드 추가
    public boolean isVerified(String phoneNumber) {
        return verifiedPhones.contains(phoneNumber);
    }

    public void removeVerified(String phoneNumber) {
        verifiedPhones.remove(phoneNumber);
    }

    private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}