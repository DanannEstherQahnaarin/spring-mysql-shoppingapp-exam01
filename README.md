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
- 장바구니 담기: 상품을 장바구니에 추가 (중복 시 수량 증가)
- 장바구니 조회: 현재 사용자의 장바구니 항목 조회
- 주문하기: 장바구니의 모든 상품을 주문 처리
  - 재고 차감 (트랜잭션 보장)
  - 주문 내역 저장
  - 장바구니 비우기

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
- `POST /api/orders/cart` - 장바구니 담기 (인증 필요)
- `GET /api/orders/cart` - 장바구니 조회 (인증 필요)
- `POST /api/orders` - 주문하기 (인증 필요)

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
- ✅ 장바구니 담기
- ✅ 장바구니 조회
- ✅ 주문하기

#### 추가 가능한 기능
- **장바구니 관리**
  - 장바구니 항목 수량 변경 (`PUT /api/orders/cart/{cartItemId}`)
  - 장바구니 항목 삭제 (`DELETE /api/orders/cart/{cartItemId}`)
  - 장바구니 전체 비우기
  
- **주문 내역 관리**
  - 주문 내역 조회 (`GET /api/orders`)
  - 주문 상세 조회 (`GET /api/orders/{orderId}`)
  - 주문 취소 (`POST /api/orders/{orderId}/cancel`)
  - 주문 상태 조회 (주문완료, 배송중, 배송완료, 취소 등)
  
- **주문 상태 관리**
  - 주문 상태 변경 (관리자)
  - 배송 정보 관리
  - 주문 취소 시 재고 복구

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
2. 장바구니 수량 변경, 항목 삭제
3. 주문 내역 조회
4. 프로필 조회/수정

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