package co.kr.allpick.domain.order.repository.impl;

import org.springframework.stereotype.Component;

import co.kr.allpick.domain.order.entity.Payment;
import co.kr.allpick.domain.order.repository.PaymentRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;


@Component("paymentRepositoryHandler")
@RequiredArgsConstructor
public class PaymentRepositoryImpl {

    private final PaymentRepository paymentRepository;

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment findByOrderId(Long orderId) {
        return paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }
}