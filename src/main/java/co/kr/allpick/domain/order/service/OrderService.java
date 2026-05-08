package co.kr.allpick.domain.order.service;

import co.kr.allpick.domain.order.dto.DeliveryAddressRequestDto;
import co.kr.allpick.domain.order.dto.DeliveryAddressResponseDto;
import co.kr.allpick.domain.order.dto.OrderCreateRequestDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.domain.order.dto.PaymentResponseDto;
import co.kr.allpick.domain.order.dto.SellerOrderResponseDto;

import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(Long memberId ,OrderCreateRequestDto request);

    List<SellerOrderResponseDto> getSellerOrders(Long memberId);

    OrderResponseDto getOrder(Long orderId);

    PaymentResponseDto getPayment(Long orderId);

    DeliveryAddressResponseDto addDeliveryAddress(Long memberId, DeliveryAddressRequestDto request);

    List<DeliveryAddressResponseDto> getDeliveryAddresses(Long memberId);

    DeliveryAddressResponseDto updateDeliveryAddress(Long memberId, Long addressId, DeliveryAddressRequestDto request);

    void deleteDeliveryAddress(Long memberId, Long addressId);
}