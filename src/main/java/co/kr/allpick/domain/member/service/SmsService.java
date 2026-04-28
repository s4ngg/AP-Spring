package co.kr.allpick.domain.member.service;

public interface SmsService {
    void sendVerificationCode(String phoneNumber);
    String verifyAndFindId(String phoneNumber, String inputCode);
    void verifyCode(String phoneNumber, String inputCode);
    boolean isVerified(String phoneNumber);
    void removeVerified(String phoneNumber);
}