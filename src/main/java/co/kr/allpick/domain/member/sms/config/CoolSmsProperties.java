package co.kr.allpick.domain.member.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "coolsms")
public class CoolSmsProperties {
    private String apiKey;
    private String apiSecret;
    private String sender;

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
}