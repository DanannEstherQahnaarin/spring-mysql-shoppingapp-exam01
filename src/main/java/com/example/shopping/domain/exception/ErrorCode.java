package com.example.shopping.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션에서 사용하는 에러 코드를 정의하는 enum 클래스
 * 
 * <p>모든 예외 상황에 대한 에러 코드, HTTP 상태 코드, 메시지를 관리합니다.
 * 
 * <p>사용 목적:
 * <ul>
 *   <li>에러 코드의 일관성 유지</li>
 *   <li>에러 메시지의 중앙 관리</li>
 *   <li>클라이언트가 에러를 식별할 수 있는 코드 제공</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // 인증 관련 에러 (400)
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH_001", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH_002", "비밀번호가 일치하지 않습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.BAD_REQUEST, "AUTH_003", "이미 사용 중인 로그인 ID입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "AUTH_004", "이미 사용 중인 이메일입니다."),
    
    // 권한 관련 에러 (400)
    NOT_HAVE_PERMISSION(HttpStatus.BAD_REQUEST, "AUTH_004", "권한이 없습니다."),

    // 상품 관련 에러 (400)
    PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PRODUCT_001", "상품을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "PRODUCT_002", "카테고리를 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "PRODUCT_003", "재고가 부족합니다."),
    
    // 장바구니 관련 에러 (400)
    CART_ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "CART_ITEM_001", "장바구니 항목을 찾을 수 없습니다."),

    // 주문 관련 에러 (400)
    CART_EMPTY(HttpStatus.BAD_REQUEST, "ORDER_001", "장바구니가 비어있습니다."),
    NO_ITEMS_TO_ORDER(HttpStatus.BAD_REQUEST, "ORDER_002", "주문할 상품이 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.BAD_REQUEST, "ORDER_003", "주문 정보를 찾을 수 없습니다."),
    ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "ORDER_004", "이미 취소된 주문입니다."),
    ADMIN_PERMISSION_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH_005", "관리자 권한이 필요합니다."),
    
    // 서버 내부 에러 (500)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_001", "서버 내부 오류가 발생했습니다.");
    
    /** HTTP 상태 코드 */
    private final HttpStatus httpStatus;
    
    /** 에러 코드 (클라이언트가 식별할 수 있는 고유 코드) */
    private final String code;
    
    /** 에러 메시지 (사용자에게 표시될 메시지) */
    private final String message;
}

