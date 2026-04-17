package co.kr.allpick.domain.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.kr.allpick.domain.order.entity.Payment;
import co.kr.allpick.domain.order.entity.Payment.PaymentMethod;
import co.kr.allpick.domain.order.entity.Payment.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "결제 응답 DTO")
public class PaymentResponseDto {

    @Schema(description = "결제 ID", example = "1")
    private Long paymentId;

    @Schema(description = "결제 키", example = "toss_payment_key_123")
    private String paymentKey;

    @Schema(description = "결제 수단", example = "CARD")
    private PaymentMethod method;

    @Schema(description = "결제 금액", example = "53000")
    private BigDecimal amount;

    @Schema(description = "결제 상태", example = "DONE")
    private PaymentStatus status;

    @Schema(description = "결제 완료 일시")
    private LocalDateTime paidAt;
    
    public static PaymentResponseDto from(Payment payment) {
        return PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .paymentKey(payment.getPaymentKey())
                .method(payment.getMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .build();
    }
}