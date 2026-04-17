package co.kr.allpick.global.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import co.kr.allpick.global.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        logger.warn("BusinessException: {} {} {}",
                e.getErrorCode().name(),
                e.getErrorCode().getStatus(),
                e.getMessage());
        return ApiResponse.fail(e.getMessage(), e.getErrorCode().getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        logger.error("Exception: {}", e.getMessage());
        return ApiResponse.fail("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("유효성 검사 실패");
        logger.warn("유효성 검사 실패 - 상태: {}, 메시지: {}", HttpStatus.BAD_REQUEST, errorMessage);
        return ApiResponse.fail(errorMessage, HttpStatus.BAD_REQUEST);
    }
}