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

- **카테고리 관리**
  - 카테고리 등록: 새로운 상품 카테고리 생성
  - 카테고리 목록 조회: 모든 카테고리 목록 조회
  - 카테고리 수정: 카테고리 이름 변경
  - 카테고리 삭제: 카테고리 제거
- **상품 관리**
  - 상품 등록: 카테고리 연동, 가격, 재고 관리
  - 상품 목록 조회: QueryDSL 기반 동적 쿼리
  - 상품 상세 조회: 특정 상품의 상세 정보 조회
  - 상품 수정: 상품 정보 변경 (관리자)
  - 상품 삭제: 상품 제거 (관리자)

### 3. 주문 관리 (Order Management)

- **장바구니 관리**
  - 장바구니 담기: 상품을 장바구니에 추가 (중복 시 수량 증가)
  - 장바구니 조회: 현재 사용자의 장바구니 항목 조회
  - 장바구니 항목 수량 변경: 장바구니에 담긴 상품의 수량 수정
  - 장바구니 항목 삭제: 장바구니에서 특정 상품 제거
  - 장바구니 전체 비우기: 사용자의 모든 장바구니 항목 삭제
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
  - 주문 상태 변경: 관리자 권한으로 주문 상태 변경 (`PATCH /api/orders/{orderId}/status`)
  - 배송 정보 관리: 택배사, 운송장 번호 등 배송 정보 업데이트

### 4. 통합 예외 처리 (Exception Handling)

- 전역 예외 처리: `@RestControllerAdvice`를 활용한 일관된 에러 응답
- 에러 코드 관리: `ErrorCode` enum을 통한 중앙화된 에러 코드 및 메시지 관리
- 커스텀 예외: `BusinessException`을 통한 비즈니스 로직 예외 처리
- 표준화된 에러 응답: 클라이언트에게 일관된 형식의 에러 정보 제공

### 서비스 흐름

```text
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

```text
Controller (REST API 엔드포인트)
    ↓
Service (비즈니스 로직, 트랜잭션 관리)
    ↓
Repository (데이터 접근 계층)
    ↓
Entity (도메인 모델)
```

### 패키지 구조

```text
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

## 추가 문서

프로젝트에 대한 더 자세한 정보는 다음 문서를 참고하세요:

- [API 엔드포인트](./docs/API.md) - 모든 API 엔드포인트 목록
- [예외 처리](./docs/EXCEPTION_HANDLING.md) - 예외 처리 구조 및 사용법
- [향후 기능](./docs/FUTURE_FEATURES.md) - 추가 및 확장 가능한 기능 목록
- [고급 가이드](./docs/ADVANCED_GUIDE.md) - 시니어 개발자를 위한 성능 최적화, 보안, 운영 가이드
