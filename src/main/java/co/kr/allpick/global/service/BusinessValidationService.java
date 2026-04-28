package co.kr.allpick.global.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

import co.kr.allpick.global.config.BusinessValidationProperties;


@Service
@RequiredArgsConstructor
public class BusinessValidationService {

    private final BusinessValidationProperties businessValidationProperties;
    @Value("${business.api.key}")
    private String apiKey;

    public boolean validateBusinessNumber(String businessNumber) {
        String cleaned = businessNumber.replaceAll("-", "");

        Map<String, Object> requestBody = Map.of("b_no", List.of(cleaned));

        WebClient webClient = WebClient.create();

        Map<String, Object> response = webClient.post()
                .uri("https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey="
                        + businessValidationProperties.getKey())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        String statusCode = (String) data.get(0).get("b_stt_cd");

        return "01".equals(statusCode);
    }
}