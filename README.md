# Shopping App Server

Spring Boot 기반의 RESTful API 쇼핑몰 서버 애플리케이션
> JWT 인증 기반 쇼핑몰 백엔드 REST API 서버 (Spring Boot / JPA / QueryDSL)

## 프로젝트 소개

이 프로젝트는 Spring Boot 기반의 쇼핑몰 백엔드 REST API 서버입니다.
**JWT 인증**, **트랜잭션 관리**, **예외 처리 구조** 등
실무에서 빈번하게 요구되는 서버 사이드 요구사항을 중심으로 구현되었습니다.

주요 목적:
- **RESTful API 설계 원칙** 이해 및 적용
- **JWT 기반 인증/인가** 구현을 통한 보안 학습
- **Spring Data JPA와 QueryDSL**을 활용한 효율적인 데이터 접근
- **트랜잭션 관리**를 통한 데이터 무결성 보장
- **계층형 아키텍처**를 통한 유지보수성 향상

## 주요 기능

### 1. 인증/인가 (Authentication & Authorization)
- 회원가입: 사용자 정보, 프로필, 인증 정보 저장
- 로그인: JWT 토큰 기반 인증
- JWT 토큰 발급 및 검증

### 2. 상품 관리 (Product Management)
- 카테고리 등록
- 상품 등록 (카테고리 연동, 가격, 재고 관리)
- 상품 목록 조회 (QueryDSL 기반 동적 쿼리)

### 3. 주문 관리 (Order Management)
- **장바구니 관리**
  - 장바구니 담기: 상품을 장바구니에 추가 (중복 시 수량 증가)
  - 장바구니 조회: 현재 사용자의 장바구니 항목 조회
  - 장바구니 항목 수량 변경: 장바구니에 담긴 상품의 수량 수정
  - 장바구니 항목 삭제: 장바구니에서 특정 상품 제거
- **주문 처리**
  - 주문하기: 장바구니의 모든 상품을 주문 처리
    - 재고 차감 (트랜잭션 보장)
    - 주문 내역 저장
    - 장바구니 비우기
- **주문 내역 관리**
  - 주문 내역 조회: 사용자의 모든 주문 목록 조회 (`GET /api/orders/list`)
  - 주문 상세 조회: 특정 주문의 상세 정보 조회 (`GET /api/orders/{orderId}/detail`)
  - 주문 취소: 주문 취소 및 재고 복구 (`POST /api/orders/{orderId}/cancel`)
- **주문 상태 관리**
  - 주문 상태 변경: 관리자 권한으로 주문 상태 변경 (서비스 레이어 구현 완료, API 엔드포인트 추가 필요)

### 4. 통합 예외 처리 (Exception Handling)
- 전역 예외 처리: `@RestControllerAdvice`를 활용한 일관된 에러 응답
- 에러 코드 관리: `ErrorCode` enum을 통한 중앙화된 에러 코드 및 메시지 관리
- 커스텀 예외: `BusinessException`을 통한 비즈니스 로직 예외 처리
- 표준화된 에러 응답: 클라이언트에게 일관된 형식의 에러 정보 제공

### 서비스 흐름

```
1. 회원가입 → 로그인 → JWT 토큰 발급
2. 상품 조회 (인증 필요)
3. 장바구니 담기 → 장바구니 조회 → 주문하기
   - 주문 시 재고 차감 및 주문 내역 저장 (원자성 보장)
```

## 기술 스택

### Backend
- **Java 21**: 최신 Java LTS 버전
- **Spring Boot 4.0.1**: 웹 애플리케이션 프레임워크
- **Spring Data JPA**: 데이터 접근 계층
- **Spring Security**: 인증/인가 프레임워크
- **QueryDSL 5.0.0**: 타입 안전한 동적 쿼리 작성

### Database
- **MySQL**: 관계형 데이터베이스

### Build & Tools
- **Gradle**: 빌드 도구
- **Lombok**: 보일러플레이트 코드 제거

### Security
- **JWT (jjwt 0.11.5)**: 토큰 기반 인증

## 아키텍처

### 계층 구조
```
Controller (REST API 엔드포인트)
    ↓
Service (비즈니스 로직, 트랜잭션 관리)
    ↓
Repository (데이터 접근 계층)
    ↓
Entity (도메인 모델)
```

### 패키지 구조
```
com.example.shopping
├── domain/                    # 도메인 계층
│   ├── controller/           # REST 컨트롤러
│   ├── service/              # 비즈니스 로직
│   ├── repository/           # 데이터 접근 (JPA, QueryDSL)
│   ├── entity/               # JPA 엔티티
│   │   ├── user/            # 사용자 관련 엔티티
│   │   ├── product/         # 상품 관련 엔티티
│   │   └── order/           # 주문 관련 엔티티
│   ├── dto/                  # 데이터 전송 객체
│   ├── exception/            # 예외 처리
│   │   ├── ErrorCode.java          # 에러 코드 enum
│   │   ├── BusinessException.java  # 커스텀 예외 클래스
│   │   ├── ErrorResponse.java      # 에러 응답 DTO
│   │   └── GlobalExceptionHandler.java  # 전역 예외 처리 핸들러
│   └── enums/                # 열거형 타입
└── global/                    # 전역 설정
    ├── config/               # 설정 클래스 (Security, QueryDSL)
    └── security/             # 보안 관련 (JWT Provider)
```

### 주요 설계 패턴
- **Repository Pattern**: 데이터 접근 로직 캡슐화
- **DTO Pattern**: 계층 간 데이터 전송
- **Builder Pattern**: 엔티티 생성 (Lombok 활용)
- **Transaction Management**: `@Transactional`을 통한 트랜잭션 관리
- **Global Exception Handling**: `@RestControllerAdvice`를 통한 전역 예외 처리

## 실행 방법

### 사전 요구사항
- Java 21 이상
- MySQL 8.0 이상
- Gradle (또는 Gradle Wrapper 사용)

### 로컬 실행

1. **데이터베이스 설정**
   - MySQL 실행 및 데이터베이스 생성
   ```sql
   CREATE DATABASE shopping CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **애플리케이션 설정**
   - `src/main/resources/application.yml` 파일에서 데이터베이스 연결 정보 확인
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/shopping
       username: scott
       password: tiger
   ```

3. **애플리케이션 실행**
   ```bash
   # Gradle Wrapper 사용
   ./gradlew bootRun
   
   # 또는 빌드 후 실행
   ./gradlew build
   java -jar build/libs/*.jar
   ```

4. **서버 접속**
   - 기본 포트: `http://localhost:8080`

### Docker 실행

Docker Compose를 사용한 실행 예시:

1. **docker-compose.yml** 파일 생성 (프로젝트 루트)
   ```yaml
   version: '3.8'
   services:
     mysql:
       image: mysql:8.0
       container_name: shopping-mysql
       environment:
         MYSQL_ROOT_PASSWORD: root
         MYSQL_DATABASE: shopping
         MYSQL_USER: scott
         MYSQL_PASSWORD: tiger
       ports:
         - "3306:3306"
       volumes:
         - mysql_data:/var/lib/mysql
   
     app:
       build: .
       container_name: shopping-app
       ports:
         - "8080:8080"
       depends_on:
         - mysql
       environment:
         SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/shopping
         SPRING_DATASOURCE_USERNAME: scott
         SPRING_DATASOURCE_PASSWORD: tiger
   
   volumes:
     mysql_data:
   ```

2. **Dockerfile** 파일 생성 (프로젝트 루트)
   ```dockerfile
   FROM openjdk:21-jdk-slim
   WORKDIR /app
   COPY build/libs/*.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

3. **실행**
   ```bash
   # 애플리케이션 빌드
   ./gradlew build
   
   # Docker Compose 실행
   docker-compose up -d
   ```

## API 엔드포인트

### 인증 (Authentication)
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인

### 상품 (Product)
- `POST /api/products/category/add` - 카테고리 등록
- `POST /api/products/add` - 상품 등록
- `GET /api/products/list` - 상품 목록 조회

### 주문 (Order)
- `POST /api/orders/cart/add` - 장바구니 담기 (인증 필요)
- `GET /api/orders/cart` - 장바구니 조회 (인증 필요)
- `PUT /api/orders/cart/update/{cartItemId}` - 장바구니 항목 수량 변경 (인증 필요)
- `DELETE /api/orders/cart/delete/{cartItemId}` - 장바구니 항목 삭제 (인증 필요)
- `POST /api/orders/create` - 주문하기 (인증 필요)
- `GET /api/orders/list` - 주문 내역 조회 (인증 필요)
- `GET /api/orders/{orderId}/detail` - 주문 상세 조회 (인증 필요)
- `POST /api/orders/{orderId}/cancel` - 주문 취소 (인증 필요)

## 예외 처리 (Exception Handling)

이 프로젝트는 통합 예외 처리 시스템을 구현하여 일관된 에러 응답을 제공합니다.

### 구조

#### 1. ErrorCode (에러 코드 Enum)
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

#### 2. BusinessException (커스텀 예외)
비즈니스 로직에서 발생하는 예외를 나타내는 커스텀 예외 클래스입니다.

```java
// 사용 예시
throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
```

#### 3. ErrorResponse (에러 응답 DTO)
클라이언트에게 반환되는 표준화된 에러 응답 형식입니다.

```json
{
  "code": "PRODUCT_001",
  "message": "상품을 찾을 수 없습니다.",
  "status": 400
}
```

#### 4. GlobalExceptionHandler (전역 예외 처리)
`@RestControllerAdvice`를 사용하여 모든 컨트롤러에서 발생하는 예외를 일관되게 처리합니다.

### 에러 코드 분류

- **인증 관련 (AUTH_xxx)**: 사용자 인증/인가 관련 에러
- **상품 관련 (PRODUCT_xxx)**: 상품 및 카테고리 관련 에러
- **주문 관련 (ORDER_xxx)**: 주문 및 장바구니 관련 에러
- **서버 에러 (SERVER_xxx)**: 내부 서버 오류

### 사용 예시

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

### 장점

- **일관성**: 모든 에러가 동일한 형식으로 반환됨
- **유지보수성**: 에러 코드와 메시지를 한 곳에서 관리
- **가독성**: 에러 코드로 에러 유형을 명확하게 식별 가능
- **확장성**: 새로운 에러 코드를 쉽게 추가 가능

## 향후 추가 및 확장 가능한 기능

이 프로젝트는 기본적인 쇼핑몰 기능을 제공하며, 다음과 같은 기능들을 추가하여 확장할 수 있습니다.

### 1. 인증/인가 기능 확장

#### 현재 구현
- ✅ 회원가입
- ✅ 로그인 (JWT 토큰 발급)

#### 추가 가능한 기능
- **토큰 관리**
  - 토큰 갱신 (Refresh Token)
  - 토큰 만료 처리
  - 로그아웃 (토큰 무효화)
  
- **사용자 계정 관리**
  - 비밀번호 변경
  - 비밀번호 찾기 (이메일 인증)
  - 회원 정보 수정
  - 회원 탈퇴
  - 프로필 조회/수정
  
- **소셜 로그인**
  - 카카오 로그인 연동
  - 네이버 로그인 연동
  - 구글 로그인 연동

### 2. 상품 관리 기능 확장

#### 현재 구현
- ✅ 카테고리 등록
- ✅ 상품 등록
- ✅ 상품 목록 조회

#### 추가 가능한 기능
- **상품 CRUD 확장**
  - 상품 상세 조회 (`GET /api/products/{id}`)
  - 상품 수정 (`PUT /api/products/{id}`)
  - 상품 삭제 (`DELETE /api/products/{id}`)
  - 카테고리 목록 조회
  - 카테고리 수정/삭제
  
- **상품 검색 및 필터링**
  - 상품명 검색
  - 카테고리별 필터링
  - 가격 범위 필터링
  - 재고 여부 필터링
  - 정렬 옵션 (가격, 이름, 등록일 등)
  
- **상품 이미지 관리**
  - 상품 이미지 업로드
  - 이미지 다중 업로드
  - 이미지 삭제
  - 이미지 URL 관리
  
- **상품 관리 고급 기능**
  - 상품 할인/프로모션
  - 상품 상태 관리 (판매중, 품절, 판매중지)
  - 상품 조회수 통계
  - 인기 상품 조회

### 3. 주문 관리 기능 확장

#### 현재 구현
- ✅ 장바구니 담기 (`POST /api/orders/cart/add`)
- ✅ 장바구니 조회 (`GET /api/orders/cart`)
- ✅ 장바구니 항목 수량 변경 (`PUT /api/orders/cart/update/{cartItemId}`)
- ✅ 장바구니 항목 삭제 (`DELETE /api/orders/cart/delete/{cartItemId}`)
- ✅ 주문하기 (`POST /api/orders/create`)
- ✅ 주문 내역 조회 (`GET /api/orders/list`)
- ✅ 주문 상세 조회 (`GET /api/orders/{orderId}/detail`)
- ✅ 주문 취소 (`POST /api/orders/{orderId}/cancel`, 재고 복구 포함)
- ✅ 주문 상태 변경 (관리자, 서비스 레이어 구현 완료, API 엔드포인트 추가 필요)

#### 추가 가능한 기능
- **장바구니 관리**
  - 장바구니 전체 비우기
  
- **주문 상태 관리**
  - 주문 상태 변경 API 엔드포인트 추가 (관리자)
  - 배송 정보 관리

### 4. 리뷰 및 평점 시스템

#### 추가 가능한 기능
- **리뷰 관리**
  - 상품 리뷰 작성 (`POST /api/reviews`)
  - 리뷰 조회 (상품별, 사용자별)
  - 리뷰 수정/삭제
  - 리뷰 좋아요/도움됨 기능
  
- **평점 시스템**
  - 평점 등록 (1~5점)
  - 평균 평점 계산
  - 평점 통계 조회

### 5. 결제 시스템

#### 추가 가능한 기능
- **결제 연동**
  - 결제 수단 선택 (카드, 계좌이체, 간편결제)
  - 결제 API 연동 (PG사 연동)
  - 결제 내역 저장
  - 결제 취소/환불 처리
  
- **주문-결제 연동**
  - 주문 생성 후 결제 프로세스
  - 결제 완료 후 주문 확정
  - 결제 실패 시 주문 취소

### 6. 관리자 기능

#### 추가 가능한 기능
- **관리자 인증**
  - 관리자 로그인
  - 관리자 권한 관리 (ROLE_ADMIN)
  
- **대시보드**
  - 매출 통계
  - 주문 통계
  - 상품 통계
  - 사용자 통계
  
- **관리 기능**
  - 사용자 관리 (조회, 수정, 삭제)
  - 주문 관리 (상태 변경, 취소)
  - 상품 관리 (CRUD)
  - 카테고리 관리

### 7. 알림 시스템

#### 추가 가능한 기능
- **알림 관리**
  - 주문 완료 알림
  - 배송 시작 알림
  - 재고 부족 알림
  - 프로모션 알림
  
- **알림 전송**
  - 이메일 알림
  - SMS 알림 (선택)
  - 푸시 알림 (선택)

### 8. 성능 최적화

#### 추가 가능한 기능
- **캐싱**
  - Redis를 활용한 상품 목록 캐싱
  - 카테고리 목록 캐싱
  - 인기 상품 캐싱
  
- **페이지네이션**
  - 상품 목록 페이지네이션
  - 주문 내역 페이지네이션
  - 리뷰 목록 페이지네이션
  
- **검색 최적화**
  - Elasticsearch 연동 (전문 검색)
  - 검색 결과 하이라이팅

### 9. 보안 강화

#### 추가 가능한 기능
- **인증 보안**
  - 로그인 시도 제한 (Rate Limiting)
  - IP 기반 접근 제한
  - 비밀번호 정책 강화
  
- **API 보안**
  - CORS 설정
  - API Rate Limiting
  - 요청 검증 강화

### 10. 로깅 및 모니터링

#### 추가 가능한 기능
- **로깅**
  - 구조화된 로깅 (JSON 형식)
  - 로그 레벨 관리
  - 에러 로그 집계
  
- **모니터링**
  - 애플리케이션 성능 모니터링 (APM)
  - 데이터베이스 쿼리 모니터링
  - API 응답 시간 모니터링

### 11. 테스트

#### 추가 가능한 기능
- **단위 테스트**
  - Service 계층 단위 테스트
  - Repository 계층 단위 테스트
  
- **통합 테스트**
  - API 통합 테스트
  - 데이터베이스 통합 테스트
  
- **테스트 커버리지**
  - 코드 커버리지 측정
  - 테스트 자동화

### 12. 문서화

#### 추가 가능한 기능
- **API 문서화**
  - Swagger/OpenAPI 문서 자동 생성
  - API 사용 예시 제공
  
- **코드 문서화**
  - JavaDoc 보완
  - 아키텍처 다이어그램

### 구현 우선순위 제안

#### Phase 1 (기본 기능 확장)
1. 상품 상세 조회, 수정, 삭제
2. ✅ 장바구니 수량 변경, 항목 삭제 (구현 완료)
3. ✅ 주문 내역 조회/상세/취소 (구현 완료)
4. 프로필 조회/수정
5. 주문 상태 변경 API 엔드포인트 추가

#### Phase 2 (사용자 경험 개선)
1. 상품 검색 및 필터링
2. 페이지네이션
3. 상품 이미지 업로드
4. 리뷰 및 평점 시스템

#### Phase 3 (고급 기능)
1. 결제 시스템 연동
2. 관리자 기능
3. 알림 시스템
4. Redis 캐싱

#### Phase 4 (운영 및 최적화)
1. 로깅 및 모니터링
2. 성능 최적화
3. 보안 강화
4. 테스트 커버리지 확대

---

## 시니어 개발자를 위한 고급 가이드

### 성능 최적화 전략

#### 1. 데이터베이스 최적화

**인덱싱 전략**
```sql
-- 주문 조회 최적화를 위한 복합 인덱스
CREATE INDEX idx_orders_user_status ON orders(user_id, status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

-- 장바구니 조회 최적화
CREATE INDEX idx_cart_items_cart_product ON cart_items(cart_id, product_id);

-- 주문 항목 조회 최적화
CREATE INDEX idx_order_items_order ON order_items(order_id);
```

**쿼리 최적화**
- N+1 문제 해결: `@EntityGraph`, `fetch join` 활용
- 배치 처리: 대량 데이터 처리 시 `@BatchSize` 활용
- 읽기 전용 쿼리: `@Transactional(readOnly = true)` 활용

**연결 풀 설정**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

#### 2. JPA 성능 최적화

**지연 로딩 vs 즉시 로딩**
- 기본적으로 `@OneToMany`, `@ManyToMany`는 `LAZY` 로딩
- 필요한 경우에만 `@EntityGraph` 또는 `fetch join` 사용

**캐싱 전략**
```java
// 2차 캐시 활성화 (Hibernate)
@Cacheable("products")
@Entity
public class Product { ... }

// Spring Cache (Redis 연동)
@Cacheable(value = "categories", key = "#root.method.name")
public List<Category> findAllCategories() { ... }
```

**배치 처리**
```java
@Modifying
@Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id")
void decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);
```

#### 3. API 응답 최적화

**페이지네이션**
```java
public Page<OrderDto.OrderResponse> getOrderList(Long userId, Pageable pageable) {
    return ordersRepository.findByUserId(userId, pageable)
        .map(this::toOrderResponse);
}
```

**DTO 변환 최적화**
- MapStruct 또는 ModelMapper 사용으로 리플렉션 오버헤드 감소
- 필요한 필드만 선택적으로 조회 (Projection 활용)

### 보안 고려사항

#### 1. 인증/인가 보안

**JWT 토큰 보안**
- Access Token: 짧은 만료 시간 (15분)
- Refresh Token: 긴 만료 시간 (7일), HTTP-only Cookie 저장
- 토큰 무효화: Redis를 활용한 블랙리스트 관리

**비밀번호 보안**
```java
// BCrypt 해싱 (강도 12)
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
String hashedPassword = encoder.encode(rawPassword);
```

**Rate Limiting**
```java
@RateLimiter(name = "login", fallbackMethod = "loginFallback")
public ResponseEntity<TokenResponse> login(LoginRequest request) { ... }
```

#### 2. API 보안

**입력 검증**
```java
@Valid @RequestBody OrderDto.AddToCart request
// Bean Validation 활용
@Min(1) @Max(999)
private Integer qty;
```

**SQL Injection 방지**
- JPA/Hibernate 사용으로 자동 방지
- QueryDSL 사용 시 파라미터 바인딩 필수

**XSS 방지**
- Spring Security 기본 설정 활용
- JSON 응답 시 자동 이스케이프

**CORS 설정**
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("https://example.com"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        return new UrlBasedCorsConfigurationSource();
    }
}
```

#### 3. 데이터 보안

**민감 정보 암호화**
- 비밀번호: BCrypt 해싱
- 개인정보: AES-256 암호화 (선택적)
- 로그: 민감 정보 마스킹

**감사 로그 (Audit Log)**
```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Orders extends BaseTimeEntity {
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String lastModifiedBy;
}
```

### 트랜잭션 관리 전략

#### 1. 트랜잭션 격리 수준

**기본 전략**
- 읽기 전용: `READ_COMMITTED` (기본값)
- 쓰기 작업: `REPEATABLE_READ` (선택적)

**데드락 방지**
```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public void createOrder(Long userId) {
    // 비관적 락 (Pessimistic Lock)
    Product product = productRepository.findByIdWithLock(productId)
        .orElseThrow(...);
    
    // 낙관적 락 (Optimistic Lock)
    @Version
    private Long version;
}
```

#### 2. 분산 트랜잭션

**Saga 패턴** (마이크로서비스 환경)
- 주문 생성 → 재고 차감 → 결제 처리
- 각 단계별 보상 트랜잭션 구현

**이벤트 기반 아키텍처**
```java
@TransactionalEventListener
public void handleOrderCreated(OrderCreatedEvent event) {
    // 비동기 이벤트 처리
    inventoryService.decreaseStock(event.getOrderItems());
}
```

### 모니터링 및 로깅

#### 1. 구조화된 로깅

**Logback 설정**
```xml
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp/>
                <version/>
                <logLevel/>
                <message/>
                <mdc/>
                <stackTrace/>
            </providers>
        </encoder>
    </appender>
</configuration>
```

**로깅 전략**
- ERROR: 예외 발생, 복구 불가능한 오류
- WARN: 예상 가능한 문제, 성능 이슈
- INFO: 비즈니스 이벤트 (주문 생성, 결제 완료)
- DEBUG: 개발 환경에서만 활성화

#### 2. APM (Application Performance Monitoring)

**Spring Boot Actuator**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

**모니터링 지표**
- API 응답 시간 (P50, P95, P99)
- 데이터베이스 쿼리 시간
- JVM 메모리 사용량
- 트랜잭션 처리량 (TPS)

#### 3. 알림 설정

**에러 알림**
- Slack/Discord 웹훅 연동
- 에러율 임계값 초과 시 알림
- 데이터베이스 연결 실패 알림

### 테스트 전략

#### 1. 테스트 피라미드

```
        /\
       /E2E\          (10%) - 통합 E2E 테스트
      /------\
     /Integration\    (20%) - 통합 테스트
    /------------\
   /   Unit Test  \   (70%) - 단위 테스트
  /----------------\
```

#### 2. 단위 테스트

**Service 계층 테스트**
```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrdersRepository ordersRepository;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    void 주문_생성_성공() {
        // given
        when(ordersRepository.save(any())).thenReturn(order);
        
        // when
        Long orderId = orderService.createOrder(userId);
        
        // then
        assertThat(orderId).isNotNull();
        verify(ordersRepository).save(any());
    }
}
```

#### 3. 통합 테스트

**@SpringBootTest 활용**
```java
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void 장바구니_담기_통합_테스트() throws Exception {
        mockMvc.perform(post("/api/orders/cart/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }
}
```

#### 4. 테스트 커버리지

**JaCoCo 설정**
```gradle
plugins {
    id 'jacoco'
}

jacoco {
    toolVersion = "0.8.8"
}

test {
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}
```

**목표 커버리지**
- 전체: 80% 이상
- Service 계층: 90% 이상
- Repository 계층: 70% 이상

### CI/CD 파이프라인

#### 1. GitHub Actions 예시

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Run tests
        run: ./gradlew test
      - name: Generate coverage report
        run: ./gradlew jacocoTestReport
      - name: Upload coverage
        uses: codecov/codecov-action@v3

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Build Docker image
        run: docker build -t shopping-app:${{ github.sha }} .
      - name: Push to registry
        run: docker push shopping-app:${{ github.sha }}

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to production
        run: |
          # 배포 스크립트
```

#### 2. 배포 전략

**Blue-Green 배포**
- 두 개의 동일한 환경 운영
- 새 버전을 Green 환경에 배포
- 트래픽 전환 후 Blue 환경 제거

**Canary 배포**
- 소수의 사용자에게만 새 버전 배포
- 모니터링 후 점진적 확대

### 데이터베이스 마이그레이션

**Flyway 설정**
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

**마이그레이션 파일 예시**
```sql
-- V1__Create_orders_table.sql
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_status (user_id, status)
);
```

### 운영 가이드

#### 1. 트러블슈팅

**성능 이슈**
1. Slow Query 로그 확인
2. 인덱스 사용 여부 확인 (`EXPLAIN` 활용)
3. N+1 문제 확인 (Hibernate 통계 활성화)

**메모리 이슈**
1. JVM 힙 덤프 생성
2. 메모리 누수 확인 (Eclipse MAT 활용)
3. GC 로그 분석

#### 2. 백업 및 복구

**데이터베이스 백업**
```bash
# MySQL 덤프
mysqldump -u user -p shopping > backup_$(date +%Y%m%d).sql

# 자동 백업 스크립트 (cron)
0 2 * * * /path/to/backup.sh
```

**복구 절차**
1. 최신 백업 확인
2. 데이터베이스 복구
3. 데이터 무결성 검증
4. 애플리케이션 재시작

#### 3. 성능 튜닝 체크리스트

- [ ] 데이터베이스 인덱스 최적화
- [ ] 쿼리 성능 분석 및 개선
- [ ] JPA N+1 문제 해결
- [ ] 캐싱 전략 적용
- [ ] 연결 풀 크기 조정
- [ ] JVM 힙 크기 최적화
- [ ] GC 튜닝
- [ ] API 응답 시간 모니터링

### 아키텍처 결정 기록 (ADR)

#### ADR-001: JWT 토큰 기반 인증 선택

**상태**: 승인됨

**배경**: 세션 기반 인증 vs 토큰 기반 인증

**결정**: JWT 토큰 기반 인증 채택

**이유**:
- 서버 확장성 (Stateless)
- 마이크로서비스 아키텍처와의 호환성
- 모바일 앱 지원 용이

**대안 고려**:
- OAuth 2.0: 복잡도 증가로 제외
- 세션 기반: 확장성 문제로 제외

#### ADR-002: QueryDSL 도입

**상태**: 승인됨

**배경**: 동적 쿼리 작성 방법 선택

**결정**: QueryDSL 도입

**이유**:
- 타입 안전성
- 컴파일 타임 에러 검출
- 복잡한 동적 쿼리 작성 용이

### 코드 품질 관리

#### 1. 정적 분석 도구

**SonarQube 설정**
```gradle
plugins {
    id "org.sonarqube" version "3.5.0"
}

sonarqube {
    properties {
        property "sonar.projectKey", "shopping-app"
        property "sonar.sources", "src/main"
        property "sonar.tests", "src/test"
    }
}
```

#### 2. 코드 리뷰 체크리스트

- [ ] 비즈니스 로직 검증
- [ ] 예외 처리 적절성
- [ ] 트랜잭션 경계 설정
- [ ] 보안 취약점 확인
- [ ] 성능 이슈 확인
- [ ] 테스트 커버리지 확인

### 성능 벤치마크

#### 1. 부하 테스트

**JMeter 스크립트 예시**
- 동시 사용자: 100명
- Ramp-up 시간: 10초
- 테스트 지속 시간: 5분

**목표 지표**
- 응답 시간: P95 < 500ms
- 에러율: < 0.1%
- TPS: > 1000

#### 2. 성능 프로파일링

**JProfiler 활용**
- CPU 사용률 분석
- 메모리 할당 패턴 분석
- 메서드 실행 시간 분석

### 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [JPA Best Practices](https://vladmihalcea.com/jpa-persistence-best-practices/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [12-Factor App](https://12factor.net/)