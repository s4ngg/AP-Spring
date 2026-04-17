package co.kr.allpick.domain.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import co.kr.allpick.domain.order.dto.OrderCreateRequestDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


// !!!!!!!!! 틀만 만들어 두었음.
@Tag(name = "Order", description = "주문 API")
public interface CartControllerDocs {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @PathVariable Long memberId,
            @RequestBody OrderCreateRequestDto request);
    
}