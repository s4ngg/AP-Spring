package co.kr.allpick.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "잘못된 입력값입니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_ITEM_NOT_FOUND", "주문 상품을 찾을 수 없습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_NOT_FOUND", "결제 정보를 찾을 수 없습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "ADDRESS_NOT_FOUND", "배송지를 찾을 수 없습니다."),
    
    // Coupon
    COUPON_ALREADY_ISSUED(HttpStatus.BAD_REQUEST, "COUPON_ALREADY_ISSUED", "이미 보유한 쿠폰입니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPON_NOT_FOUND", "쿠폰을 찾을 수 없습니다."),
    COUPON_CODE_DUPLICATE(HttpStatus.BAD_REQUEST, "COUPON_CODE_DUPLICATE", "이미 존재하는 쿠폰 코드입니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "COUPON_ALREADY_USED", "이미 사용된 쿠폰입니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "COUPON_EXPIRED", "만료된 쿠폰입니다."),
	
	// Product
	PRODUCT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PRODUCT_ALREADY_EXISTS", "이미 존재하는 상품입니다. "),
	PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PRODUCT_NOT_FOUND", "존재하지 않는 상품입니다.");
	
    private final HttpStatus status;
    private final String code;
    private final String message;
}



