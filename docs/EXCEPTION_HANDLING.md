# 예외 처리 (Exception Handling)

이 프로젝트는 통합 예외 처리 시스템을 구현하여 일관된 에러 응답을 제공합니다.

## 구조

### 1. ErrorCode (에러 코드 Enum)

모든 예외 상황에 대한 에러 코드, HTTP 상태 코드, 메시지를 중앙에서 관리합니다.

```java
public enum ErrorCode {
    // 인증 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH_001", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH_002", "비밀번호가 일치하지 않습니다."),
    
    // 상품 관련 에러
    PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PRODUCT_001", "상품을 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "PRODUCT_003", "재고가 부족합니다."),
    
    // 주문 관련 에러
    CART_EMPTY(HttpStatus.BAD_REQUEST, "ORDER_001", "장바구니가 비어있습니다."),
    // ...
}
```

### 2. BusinessException (커스텀 예외)

비즈니스 로직에서 발생하는 예외를 나타내는 커스텀 예외 클래스입니다.

```java
// 사용 예시
throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
```

### 3. ErrorResponse (에러 응답 DTO)

클라이언트에게 반환되는 표준화된 에러 응답 형식입니다.

```json
{
  "code": "PRODUCT_001",
  "message": "상품을 찾을 수 없습니다.",
  "status": 400
}
```

### 4. GlobalExceptionHandler (전역 예외 처리)

`@RestControllerAdvice`를 사용하여 모든 컨트롤러에서 발생하는 예외를 일관되게 처리합니다.

## 에러 코드 분류

- **인증 관련 (AUTH_xxx)**: 사용자 인증/인가 관련 에러
- **상품 관련 (PRODUCT_xxx)**: 상품 및 카테고리 관련 에러
- **주문 관련 (ORDER_xxx)**: 주문 및 장바구니 관련 에러
- **서버 에러 (SERVER_xxx)**: 내부 서버 오류

## 사용 예시

비즈니스 로직에서 예외를 발생시키면:

```java
// Service 계층
Product product = productRepository.findById(productId)
    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
```

GlobalExceptionHandler가 자동으로 처리하여 클라이언트에게 다음과 같은 응답을 반환합니다:

```json
HTTP 400 Bad Request
{
  "code": "PRODUCT_001",
  "message": "상품을 찾을 수 없습니다.",
  "status": 400
}
```

## 장점

- **일관성**: 모든 에러가 동일한 형식으로 반환됨
- **유지보수성**: 에러 코드와 메시지를 한 곳에서 관리
- **가독성**: 에러 코드로 에러 유형을 명확하게 식별 가능
- **확장성**: 새로운 에러 코드를 쉽게 추가 가능

