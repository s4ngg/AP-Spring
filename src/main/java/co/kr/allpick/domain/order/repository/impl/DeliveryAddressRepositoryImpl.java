package co.kr.allpick.domain.order.repository.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import co.kr.allpick.domain.order.entity.DeliveryAddress;
import co.kr.allpick.domain.order.repository.DeliveryAddressRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeliveryAddressRepositoryImpl {

    private final DeliveryAddressRepository deliveryAddressRepository;

    public DeliveryAddress save(DeliveryAddress deliveryAddress) {
        return deliveryAddressRepository.save(deliveryAddress);
    }

    public DeliveryAddress findById(Long addressId) {
        return deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));
    }

    public List<DeliveryAddress> findByMemberId(Long memberId) {
        return deliveryAddressRepository.findByMemberId(memberId);
    }
}