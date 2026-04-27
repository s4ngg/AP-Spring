package co.kr.allpick.domain.seller.service;

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