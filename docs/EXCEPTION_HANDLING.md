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
    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH_006", "현재 비밀번호가 일치하지 않습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "AUTH_007", "잘못된 인증코드입니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "AUTH_008", "인증코드가 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "AUTH_010", "Refresh Token이 유효하지 않습니다."),
    TOKEN_USER_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH_011", "토큰의 유저 정보가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "AUTH_012", "잘못된 토큰입니다."),
    USER_LOGGED_OUT(HttpStatus.BAD_REQUEST, "AUTH_013", "로그아웃 된 사용자입니다."),
    USER_PROFILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH_014", "프로필을 찾을 수 없습니다."),
    
    // 권한 관련 에러
    ADMIN_PERMISSION_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH_005", "관리자 권한이 필요합니다."),
    NOT_HAVE_PERMISSION(HttpStatus.BAD_REQUEST, "AUTH_015", "권한이 없습니다."),
    INVALID_USER_STATUS(HttpStatus.BAD_REQUEST, "AUTH_009", "잘못된 상태 값입니다."),
    
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
  - AUTH_001: 사용자를 찾을 수 없음
  - AUTH_002: 비밀번호 불일치
  - AUTH_005: 관리자 권한 필요
  - AUTH_006: 현재 비밀번호 불일치
  - AUTH_007: 잘못된 인증코드
  - AUTH_008: 인증코드 만료
  - AUTH_009: 잘못된 사용자 상태 값
  - AUTH_010: Refresh Token 무효
  - AUTH_011: 토큰과 사용자 정보 불일치
  - AUTH_012: 잘못된 토큰
  - AUTH_013: 로그아웃 된 사용자
  - AUTH_014: 프로필 없음
  - AUTH_015: 권한 없음
- **상품 관련 (PRODUCT_xxx)**: 상품 및 카테고리 관련 에러
- **주문 관련 (ORDER_xxx)**: 주문 및 장바구니 관련 에러
- **서버 에러 (SERVER_xxx)**: 내부 서버 오류

## 예외 처리 전환 완료

✅ **domain 폴더 내 모든 `RuntimeException`을 `BusinessException`으로 전환 완료**

모든 서비스 계층(UserService, OrderService, StatisticsService, AuthService)에서 발생하는 예외가 `BusinessException`을 사용하도록 전환되어 일관된 예외 처리가 이루어지고 있습니다.

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

