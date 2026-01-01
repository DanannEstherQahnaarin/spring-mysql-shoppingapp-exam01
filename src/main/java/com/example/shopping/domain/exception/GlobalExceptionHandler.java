package com.example.shopping.domain.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * 전역 예외 처리 핸들러
 * 
 * <p>애플리케이션 전역에서 발생하는 예외를 처리하여 클라이언트에게
 * 일관된 형식의 에러 응답을 반환합니다.
 * 
 * <p>처리 방식:
 * <ul>
 *   <li>@RestControllerAdvice: 모든 @RestController에서 발생하는 예외를 처리</li>
 *   <li>@ExceptionHandler: 특정 예외 타입에 대한 처리 메서드 지정</li>
 *   <li>BusinessException: 비즈니스 로직에서 발생하는 예외를 ErrorResponse로 변환</li>
 * </ul>
 * 
 * <p>응답 형식:
 * <pre>
 * HTTP Status: 에러 코드에 정의된 상태 코드
 * Body: {
 *   "code": "에러 코드",
 *   "message": "에러 메시지",
 *   "status": HTTP 상태 코드
 * }
 * </pre>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.example.shopping.domain.controller")
public class GlobalExceptionHandler {
    
    /**
     * BusinessException을 처리합니다.
     * 
     * <p>비즈니스 로직에서 발생하는 예외를 클라이언트에게 전달합니다.
     * ErrorCode에 정의된 HTTP 상태 코드와 메시지를 사용하여 응답합니다.
     * 
     * @param e 발생한 BusinessException
     * @return ErrorResponse를 포함한 ResponseEntity
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("BusinessException 발생: {} - {}", e.getErrorCode().getCode(), e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(errorResponse);
    }
    
    /**
     * 기타 모든 예외를 처리합니다.
     * 
     * <p>예상치 못한 예외가 발생한 경우 내부 서버 에러로 처리합니다.
     * 
     * @param e 발생한 예외
     * @return ErrorResponse를 포함한 ResponseEntity (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예상치 못한 예외 발생", e);
        
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
            .body(errorResponse);
    }
}

