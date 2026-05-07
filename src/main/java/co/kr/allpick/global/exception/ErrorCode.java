package co.kr.allpick.global.exception;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "잘못된 입력값입니다."),
    APPROVAL_NOT_PENDING(HttpStatus.BAD_REQUEST, "APPROVAL_NOT_PENDING", "승인 대기 상태의 항목만 처리할 수 있습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_ITEM_NOT_FOUND", "주문 상품을 찾을 수 없습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_NOT_FOUND", "결제 정보를 찾을 수 없습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "ADDRESS_NOT_FOUND", "배송지를 찾을 수 없습니다."),
    DELIVERY_ADDRESS_DUPLICATE(HttpStatus.CONFLICT, "DELIVERY_ADDRESS_DUPLICATE", "이미 등록된 배송지입니다."),
    UNAUTHORIZED_ADDRESS(HttpStatus.FORBIDDEN, "UNAUTHORIZED_ADDRESS", "본인의 배송지만 수정/삭제할 수 있습니다."),
    ADDRESS_CANNOT_MODIFY(HttpStatus.BAD_REQUEST, "ADDRESS_CANNOT_MODIFY", "주문에 사용된 배송지는 수정/삭제할 수 없습니다."),

    // Coupon
    COUPON_ALREADY_ISSUED(HttpStatus.BAD_REQUEST, "COUPON_ALREADY_ISSUED", "이미 보유한 쿠폰입니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPON_NOT_FOUND", "쿠폰을 찾을 수 없습니다."),
    COUPON_CODE_DUPLICATE(HttpStatus.BAD_REQUEST, "COUPON_CODE_DUPLICATE", "이미 존재하는 쿠폰 코드입니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "COUPON_ALREADY_USED", "이미 사용된 쿠폰입니다."),
    INVALID_DISCOUNT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_DISCOUNT_VALUE", "PERCENT 할인값은 1~100 사이여야 합니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "COUPON_EXPIRED", "만료된 쿠폰입니다."),
    MEMBER_COUPON_NOT_FOUND(HttpStatus.NOT_FOUND,"MEMBER_COUPON_NOT_FOUND", "쿠폰을 보유하지 않은 사용자입니다."),
    
    // Inquiry
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "INQUIRY_NOT_FOUND", "문의를 찾을 수 없습니다."),
    INQUIRY_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "INQUIRY_ALREADY_DELETED", "이미 삭제된 문의입니다."),
    INQUIRY_ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "INQUIRY_ANSWER_NOT_FOUND", "문의 답변을 찾을 수 없습니다."),
    INQUIRY_UNAUTHORIZED(HttpStatus.FORBIDDEN, "INQUIRY_UNAUTHORIZED", "해당 문의에 대한 권한이 없습니다."),
    INQUIRY_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "INQUIRY_CANNOT_CANCEL", "접수 대기 상태에서만 취소 가능합니다."),

    // Claim
    CLAIM_NOT_FOUND(HttpStatus.NOT_FOUND, "CLAIM_NOT_FOUND", "클레임을 찾을 수 없습니다."),
    CLAIM_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "CLAIM_ALREADY_DELETED", "이미 삭제된 클레임입니다."),
    CLAIM_INVALID_STATUS(HttpStatus.BAD_REQUEST, "CLAIM_INVALID_STATUS", "유효하지 않은 클레임 상태입니다."),
    CLAIM_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "CLAIM_ALREADY_COMPLETED", "이미 처리 완료된 클레임입니다."),

    CLAIM_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "CLAIM_ALREADY_CANCELLED", "이미 취소된 클레임입니다."),
    CLAIM_ALREADY_EXISTS(HttpStatus.CONFLICT, "CLAIM_ALREADY_EXISTS", "이미 처리 중인 클레임이 존재합니다."),
    CLAIM_REASON_MISMATCH(HttpStatus.BAD_REQUEST, "CLAIM_REASON_MISMATCH", "클레임 유형과 사유 코드가 일치하지 않습니다."),
    CLAIM_UNAUTHORIZED(HttpStatus.FORBIDDEN, "CLAIM_UNAUTHORIZED", "본인의 클레임만 취소할 수 있습니다."),

    // Attachment
    ATTACHMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ATTACHMENT_NOT_FOUND", "첨부파일을 찾을 수 없습니다."),
    ATTACHMENT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "ATTACHMENT_ALREADY_DELETED", "이미 삭제된 첨부파일입니다."),
    ATTACHMENT_UNAUTHORIZED(HttpStatus.FORBIDDEN, "ATTACHMENT_UNAUTHORIZED", "본인의 첨부파일만 삭제할 수 있습니다."),

    // Seller

    NOT_SELLER(HttpStatus.FORBIDDEN, "NOT_SELLER", "판매자 권한이 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, "UNAUTHORIZED", "본인만 접근 가능합니다."),
    PRODUCT_NOT_OWNED(HttpStatus.FORBIDDEN, "PRODUCT_NOT_OWNED", "해당 상품에 대한 권한이 없습니다."),
    
    // SMS
    SMS_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SMS_SEND_FAILED", "SMS 발송에 실패했습니다."),
    PHONE_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "PHONE_NOT_VERIFIED", "핸드폰 인증이 필요합니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_CODE", "인증번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND_BY_PHONE(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND_BY_PHONE", "해당 번호로 가입된 계정이 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "INVALID_PASSWORD", "이메일 또는 비밀번호가 틀렸습니다."),
    CURRENT_PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "CURRENT_PASSWORD_MISMATCH", "현재 비밀번호가 일치하지 않습니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "SAME_PASSWORD", "현재 비밀번호와 동일한 비밀번호로 변경할 수 없습니다."),

    // Admin
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "ADMIN_NOT_FOUND", "관리자를 찾을 수 없습니다."),
    ADMIN_EMAIL_DUPLICATED(HttpStatus.CONFLICT, "ADMIN_DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
    ADMIN_BLOCKED(HttpStatus.FORBIDDEN, "ADMIN_BLOCKED", "차단된 관리자 계정입니다."),
    ADMIN_FORBIDDEN(HttpStatus.FORBIDDEN, "ADMIN_FORBIDDEN", "SUPER_ADMIN 권한이 필요합니다."),
    ADMIN_CANNOT_BLOCK_SELF(HttpStatus.BAD_REQUEST, "ADMIN_CANNOT_BLOCK_SELF", "본인 계정의 상태는 변경할 수 없습니다."),

    // FAQ
    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "FAQ_NOT_FOUND", "FAQ를 찾을 수 없습니다."),


	//Seller
	DUPLICATE_BUSINESS_NUMBER(HttpStatus.CONFLICT, "DUPLICATE_BUSINESS_NUMBER", "이미 등록된 사업자등록번호입니다."),
    INVALID_BUSINESS_NUMBER(HttpStatus.BAD_REQUEST, "INVALID_BUSINESS_NUMBER", "유효하지 않은 사업자등록번호입니다."),
    SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, "SELLER_NOT_FOUND", "판매자를 찾을 수 없습니다."),
    SELLER_NOT_APPROVED(HttpStatus.BAD_REQUEST, "SELLER_NOT_APPROVED", "승인된 판매자의 상품만 처리할 수 있습니다."),
    SELLER_ALREADY_EXISTS(HttpStatus.CONFLICT, "SELLER_ALREADY_EXISTS", "이미 판매자로 등록된 회원입니다."),
    // Notice
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE_NOT_FOUND", "공지사항을 찾을 수 없습니다."),
    NOTICE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "NOTICE_ALREADY_DELETED", "이미 삭제된 공지사항입니다."),

    // Product
    PRODUCT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PRODUCT_ALREADY_EXISTS", "이미 존재하는 상품입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "존재하지 않는 상품입니다."),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "PRODUCT_OUT_OF_STOCK", "재고가 부족합니다."),

    // ProductOption
    PRODUCT_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND,"PRODUCT_OPTION_NOT_FOUND", "존재하지 않는 상품 옵션입니다."),
	
	// Cart 
	MEMBER_CART_NOT_FOUND(HttpStatus.NOT_FOUND,"CART_NOT_FOUND", "해당 사용자의 장바구니가 존재하지 않습니다."),

	// CartItem
	CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_ITEM_NOT_FOUND","장바구니에 해당 상품이 존재하지 않습니다."),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "존재하지 않는 카테고리입니다."),

    // image
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3_UPLOAD_FAILED", "이미지 업로드에 실패했습니다."),
    INVALID_FILE(HttpStatus.BAD_REQUEST, "INVALID_FILE", "파일이 없거나 비어있습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "INVALID_FILE_TYPE", "이미지 파일만 업로드 가능합니다."),


    // Review
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"REVIEW_ALREADY_EXISTS", "하나의 상품에 한번의 리뷰만 가능합니다."),
	REVIEW_NOT_AUTHOR(HttpStatus.FORBIDDEN, "REVIEW_NOT_AUTHOR", "해당 리뷰를 수정할 권한이 없습니다.");
	

	
    
    private final HttpStatus status;
    private final String code;
    private final String message;
}
