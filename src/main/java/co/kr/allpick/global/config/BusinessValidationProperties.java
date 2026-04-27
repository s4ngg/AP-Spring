package co.kr.allpick.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "business.api") // ✅ @ConfigurationProperties
public class BusinessValidationProperties {
    private String key;
}