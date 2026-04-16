package co.kr.allpick.global.exception;

import co.kr.allpick.global.response.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}