package co.kr.allpick.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CoolSmsConfig {

    private final CoolSmsProperties coolSmsProperties;

    @Bean
    public DefaultMessageService messageService() {
        return NurigoApp.INSTANCE.initialize(
            coolSmsProperties.getApiKey(),
            coolSmsProperties.getApiSecret(),
            "https://api.coolsms.co.kr"
        );
    }
}