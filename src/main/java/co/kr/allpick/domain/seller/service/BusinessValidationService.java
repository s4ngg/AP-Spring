package co.kr.allpick.domain.seller.service; // ✅ 패키지 이동


import co.kr.allpick.domain.seller.service.BusinessValidationProperties;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BusinessValidationService {

    private static final Logger logger = LogManager.getLogger(BusinessValidationService.class);

    private final WebClient webClient;
    private final BusinessValidationProperties properties;

    @SuppressWarnings("unchecked")
    public boolean validateBusinessNumber(String businessNumber) {
        String cleaned = businessNumber.replaceAll("-", "");
        logger.info("[BusinessValidation] 사업자번호 검증 요청 - {}", cleaned);

        Map<String, Object> requestBody = Map.of("b_no", List.of(cleaned));

        Map<String, Object> response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("api.odcloud.kr")
                    .path("/api/nts-businessman/v1/status")
                    .queryParam("serviceKey", properties.getKey())
                    .build())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        // ✅ null 체크
        if (response == null || !response.containsKey("data")) {
            logger.error("[BusinessValidation] 응답 없음 또는 data 필드 누락");
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

        if (data == null || data.isEmpty()) {
            logger.warn("[BusinessValidation] data 비어있음");
            return false;
        }

        String statusCode = (String) data.get(0).get("b_stt_cd");
        logger.info("[BusinessValidation] 검증 결과 - statusCode: {}", statusCode);
        return "01".equals(statusCode);
    }
}