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
    DELIVERY_ADDRESS_DUPLICATE(HttpStatus.CONFLICT, "DELIVERY_ADDRESS_DUPLICATE", "이미 등록된 배송지입니다."),

    // Coupon
    COUPON_ALREADY_ISSUED(HttpStatus.BAD_REQUEST, "COUPON_ALREADY_ISSUED", "이미 보유한 쿠폰입니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPON_NOT_FOUND", "쿠폰을 찾을 수 없습니다."),
    COUPON_CODE_DUPLICATE(HttpStatus.BAD_REQUEST, "COUPON_CODE_DUPLICATE", "이미 존재하는 쿠폰 코드입니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "COUPON_ALREADY_USED", "이미 사용된 쿠폰입니다."),
    INVALID_DISCOUNT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_DISCOUNT_VALUE", "PERCENT 할인값은 1~100 사이여야 합니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "COUPON_EXPIRED", "만료된 쿠폰입니다."),

    // Inquiry
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "INQUIRY_NOT_FOUND", "문의를 찾을 수 없습니다."),
    INQUIRY_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "INQUIRY_ALREADY_DELETED", "이미 삭제된 문의입니다."),
    INQUIRY_ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "INQUIRY_ANSWER_NOT_FOUND", "문의 답변을 찾을 수 없습니다."),
    INQUIRY_UNAUTHORIZED(HttpStatus.FORBIDDEN, "INQUIRY_UNAUTHORIZED", "해당 문의에 대한 권한이 없습니다."),
    INQUIRY_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "INQUIRY_CANNOT_CANCEL", "접수 대기 상태에서만 취소 가능합니다."),

    // Claim
    CLAIM_NOT_FOUND(HttpStatus.NOT_FOUND, "CLAIM_NOT_FOUND", "클레임을 찾을 수 없습니다."),
    CLAIM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "CLAIM_ALREADY_EXISTS", "이미 진행 중인 클레임이 존재합니다."),
    CLAIM_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "CLAIM_ALREADY_DELETED", "이미 삭제된 클레임입니다."),
    CLAIM_INVALID_STATUS(HttpStatus.BAD_REQUEST, "CLAIM_INVALID_STATUS", "접수 중 이상의 클레임은 취소할 수 없습니다."),
    CLAIM_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "CLAIM_ALREADY_COMPLETED", "이미 처리 완료된 클레임입니다."),
    CLAIM_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "CLAIM_ALREADY_CANCELLED", "취소된 클레임은 처리할 수 없습니다."),
    CLAIM_UNAUTHORIZED(HttpStatus.FORBIDDEN, "CLAIM_UNAUTHORIZED", "해당 클레임에 대한 권한이 없습니다."),
    CLAIM_REASON_MISMATCH(HttpStatus.BAD_REQUEST, "CLAIM_REASON_MISMATCH", "신청 유형에 맞지 않는 사유입니다."),

    NOT_SELLER(HttpStatus.FORBIDDEN, "NOT_SELLER", "판매자 권한이 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "INVALID_PASSWORD", "이메일 또는 비밀번호가 틀렸습니다."),

    //Admin
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "ADMIN_NOT_FOUND", "관리자를 찾을 수 없습니다."),
    ADMIN_EMAIL_DUPLICATED(HttpStatus.CONFLICT, "ADMIN_DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다.");

	
    // Product
    PRODUCT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PRODUCT_ALREADY_EXISTS", "이미 존재하는 상품입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "존재하지 않는 상품입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}