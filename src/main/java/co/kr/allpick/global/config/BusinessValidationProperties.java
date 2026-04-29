package co.kr.allpick.global.config;  // ✅ global/config로 변경

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "business.api")
public class BusinessValidationProperties {
    private String key;
}