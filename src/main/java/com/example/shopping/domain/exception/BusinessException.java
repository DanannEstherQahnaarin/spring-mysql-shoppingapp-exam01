package com.example.shopping.domain.exception;

/**
 * 비즈니스 로직에서 발생하는 예외를 나타내는 커스텀 예외 클래스
 * 
 * <p>런타임 예외를 상속받아 체크 예외가 아닌 언체크 예외로 동작합니다.
 * 트랜잭션 롤백을 위해 RuntimeException을 상속받습니다.
 * 
 * <p>사용 목적:
 * <ul>
 *   <li>비즈니스 로직에서 발생하는 예외를 명확하게 구분</li>
 *   <li>에러 코드를 통한 일관된 에러 처리</li>
 *   <li>예외 발생 시 적절한 HTTP 상태 코드와 메시지 제공</li>
 * </ul>
 * 
 * <p>사용 예:
 * <pre>
 * throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
 * </pre>
 * 
 * @author shopping-server
 * @since 1.0
 */
public class BusinessException extends RuntimeException {
    
    /** 에러 코드 */
    private final ErrorCode errorCode;
    
    /**
     * 에러 코드만으로 예외를 생성합니다.
     * 
     * @param errorCode 에러 코드 (ErrorCode enum)
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    /**
     * 에러 코드와 추가 메시지로 예외를 생성합니다.
     * 
     * @param errorCode 에러 코드 (ErrorCode enum)
     * @param message 추가 상세 메시지
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 에러 코드를 반환합니다.
     * 
     * @return ErrorCode enum
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

