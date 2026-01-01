package com.example.shopping.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 클라이언트에게 반환되는 에러 응답 DTO 클래스
 * 
 * <p>예외 발생 시 클라이언트에게 일관된 형식으로 에러 정보를 전달합니다.
 * 
 * <p>응답 형식:
 * <pre>
 * {
 *   "code": "PRODUCT_001",
 *   "message": "상품을 찾을 수 없습니다.",
 *   "status": 400
 * }
 * </pre>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    
    /** 에러 코드 (클라이언트가 식별할 수 있는 고유 코드) */
    private String code;
    
    /** 에러 메시지 (사용자에게 표시될 메시지) */
    private String message;
    
    /** HTTP 상태 코드 */
    private int status;
    
    /**
     * ErrorCode enum으로부터 ErrorResponse를 생성합니다.
     * 
     * @param errorCode 에러 코드
     * @return ErrorResponse 객체
     */
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
            errorCode.getCode(),
            errorCode.getMessage(),
            errorCode.getHttpStatus().value()
        );
    }
    
    /**
     * ErrorCode enum과 커스텀 메시지로 ErrorResponse를 생성합니다.
     * 
     * @param errorCode 에러 코드
     * @param message 커스텀 메시지
     * @return ErrorResponse 객체
     */
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
            errorCode.getCode(),
            message,
            errorCode.getHttpStatus().value()
        );
    }
}

