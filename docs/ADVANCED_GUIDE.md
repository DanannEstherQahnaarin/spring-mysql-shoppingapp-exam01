# 시니어 개발자를 위한 고급 가이드

## 성능 최적화 전략

### 1. 데이터베이스 최적화

#### 인덱싱 전략

```sql
-- 주문 조회 최적화를 위한 복합 인덱스
CREATE INDEX idx_orders_user_status ON orders(user_id, status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

-- 장바구니 조회 최적화
CREATE INDEX idx_cart_items_cart_product ON cart_items(cart_id, product_id);

-- 주문 항목 조회 최적화
CREATE INDEX idx_order_items_order ON order_items(order_id);
```

#### 쿼리 최적화

- N+1 문제 해결: `@EntityGraph`, `fetch join` 활용
- 배치 처리: 대량 데이터 처리 시 `@BatchSize` 활용
- 읽기 전용 쿼리: `@Transactional(readOnly = true)` 활용

#### 연결 풀 설정

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

### 2. JPA 성능 최적화

#### 지연 로딩 vs 즉시 로딩

- 기본적으로 `@OneToMany`, `@ManyToMany`는 `LAZY` 로딩
- 필요한 경우에만 `@EntityGraph` 또는 `fetch join` 사용

#### 캐싱 전략

```java
// 2차 캐시 활성화 (Hibernate)
@Cacheable("products")
@Entity
public class Product { ... }

// Spring Cache (Redis 연동)
@Cacheable(value = "categories", key = "#root.method.name")
public List<Category> findAllCategories() { ... }
```

#### 배치 처리

```java
@Modifying
@Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id")
void decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);
```

### 3. API 응답 최적화

#### 페이지네이션

```java
public Page<OrderDto.OrderResponse> getOrderList(Long userId, Pageable pageable) {
    return ordersRepository.findByUserId(userId, pageable)
        .map(this::toOrderResponse);
}
```

#### DTO 변환 최적화

- MapStruct 또는 ModelMapper 사용으로 리플렉션 오버헤드 감소
- 필요한 필드만 선택적으로 조회 (Projection 활용)

## 보안 고려사항

### 1. 인증/인가 보안

#### JWT 토큰 보안

- Access Token: 짧은 만료 시간 (15분)
- Refresh Token: 긴 만료 시간 (7일), HTTP-only Cookie 저장
- 토큰 무효화: Redis를 활용한 블랙리스트 관리

#### 비밀번호 보안

```java
// BCrypt 해싱 (강도 12)
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
String hashedPassword = encoder.encode(rawPassword);
```

#### Rate Limiting

```java
@RateLimiter(name = "login", fallbackMethod = "loginFallback")
public ResponseEntity<TokenResponse> login(LoginRequest request) { ... }
```

### 2. API 보안

#### 입력 검증

```java
@Valid @RequestBody OrderDto.AddToCart request
// Bean Validation 활용
@Min(1) @Max(999)
private Integer qty;
```

#### SQL Injection 방지

- JPA/Hibernate 사용으로 자동 방지
- QueryDSL 사용 시 파라미터 바인딩 필수

#### XSS 방지

- Spring Security 기본 설정 활용
- JSON 응답 시 자동 이스케이프

#### CORS 설정

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

### 3. 데이터 보안

#### 민감 정보 암호화

- 비밀번호: BCrypt 해싱
- 개인정보: AES-256 암호화 (선택적)
- 로그: 민감 정보 마스킹

#### 감사 로그 (Audit Log)

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

## 트랜잭션 관리 전략

### 1. 트랜잭션 격리 수준

#### 기본 전략

- 읽기 전용: `READ_COMMITTED` (기본값)
- 쓰기 작업: `REPEATABLE_READ` (선택적)

#### 데드락 방지

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

### 2. 분산 트랜잭션

#### Saga 패턴 (마이크로서비스 환경)

- 주문 생성 → 재고 차감 → 결제 처리
- 각 단계별 보상 트랜잭션 구현

#### 이벤트 기반 아키텍처

```java
@TransactionalEventListener
public void handleOrderCreated(OrderCreatedEvent event) {
    // 비동기 이벤트 처리
    inventoryService.decreaseStock(event.getOrderItems());
}
```

## 모니터링 및 로깅

### 1. 구조화된 로깅

#### Logback 설정

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

#### 로깅 전략

- ERROR: 예외 발생, 복구 불가능한 오류
- WARN: 예상 가능한 문제, 성능 이슈
- INFO: 비즈니스 이벤트 (주문 생성, 결제 완료)
- DEBUG: 개발 환경에서만 활성화

### 2. APM (Application Performance Monitoring)

#### Spring Boot Actuator

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

#### 모니터링 지표

- API 응답 시간 (P50, P95, P99)
- 데이터베이스 쿼리 시간
- JVM 메모리 사용량
- 트랜잭션 처리량 (TPS)

### 3. 알림 설정

#### 에러 알림

- Slack/Discord 웹훅 연동
- 에러율 임계값 초과 시 알림
- 데이터베이스 연결 실패 알림

## 테스트 전략

### 1. 테스트 피라미드

```text
        /\
       /E2E\          (10%) - 통합 E2E 테스트
      /------\
     /Integration\    (20%) - 통합 테스트
    /------------\
   /   Unit Test  \   (70%) - 단위 테스트
  /----------------\
```

### 2. 단위 테스트

#### Service 계층 테스트

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

### 3. 통합 테스트

#### @SpringBootTest 활용

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

### 4. 테스트 커버리지

#### JaCoCo 설정

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

#### 목표 커버리지

- 전체: 80% 이상
- Service 계층: 90% 이상
- Repository 계층: 70% 이상

## CI/CD 파이프라인

### 1. GitHub Actions 예시

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

### 2. 배포 전략

#### Blue-Green 배포

- 두 개의 동일한 환경 운영
- 새 버전을 Green 환경에 배포
- 트래픽 전환 후 Blue 환경 제거

#### Canary 배포

- 소수의 사용자에게만 새 버전 배포
- 모니터링 후 점진적 확대

## 데이터베이스 마이그레이션

### Flyway 설정

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### 마이그레이션 파일 예시

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

## 운영 가이드

### 1. 트러블슈팅

#### 성능 이슈

1. Slow Query 로그 확인
2. 인덱스 사용 여부 확인 (`EXPLAIN` 활용)
3. N+1 문제 확인 (Hibernate 통계 활성화)

#### 메모리 이슈

1. JVM 힙 덤프 생성
2. 메모리 누수 확인 (Eclipse MAT 활용)
3. GC 로그 분석

### 2. 백업 및 복구

#### 데이터베이스 백업

```bash
# MySQL 덤프
mysqldump -u user -p shopping > backup_$(date +%Y%m%d).sql

# 자동 백업 스크립트 (cron)
0 2 * * * /path/to/backup.sh
```

#### 복구 절차

1. 최신 백업 확인
2. 데이터베이스 복구
3. 데이터 무결성 검증
4. 애플리케이션 재시작

### 3. 성능 튜닝 체크리스트

- [ ] 데이터베이스 인덱스 최적화
- [ ] 쿼리 성능 분석 및 개선
- [ ] JPA N+1 문제 해결
- [ ] 캐싱 전략 적용
- [ ] 연결 풀 크기 조정
- [ ] JVM 힙 크기 최적화
- [ ] GC 튜닝
- [ ] API 응답 시간 모니터링

## 아키텍처 결정 기록 (ADR)

### ADR-001: JWT 토큰 기반 인증 선택

#### 상태

승인됨

#### 배경

세션 기반 인증 vs 토큰 기반 인증

#### 결정

JWT 토큰 기반 인증 채택

#### 이유

- 서버 확장성 (Stateless)
- 마이크로서비스 아키텍처와의 호환성
- 모바일 앱 지원 용이

#### 대안 고려

- OAuth 2.0: 복잡도 증가로 제외
- 세션 기반: 확장성 문제로 제외

### ADR-002: QueryDSL 도입

#### QueryDSL 상태

승인됨

#### QueryDSL 배경

동적 쿼리 작성 방법 선택

#### QueryDSL 결정

QueryDSL 도입

#### QueryDSL 이유

- 타입 안전성
- 컴파일 타임 에러 검출
- 복잡한 동적 쿼리 작성 용이

## 코드 품질 관리

### 1. 정적 분석 도구

#### SonarQube 설정

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

### 2. 코드 리뷰 체크리스트

- [ ] 비즈니스 로직 검증
- [ ] 예외 처리 적절성
- [ ] 트랜잭션 경계 설정
- [ ] 보안 취약점 확인
- [ ] 성능 이슈 확인
- [ ] 테스트 커버리지 확인

## 성능 벤치마크

### 1. 부하 테스트

#### JMeter 스크립트 예시

- 동시 사용자: 100명
- Ramp-up 시간: 10초
- 테스트 지속 시간: 5분

#### 목표 지표

- 응답 시간: P95 < 500ms
- 에러율: < 0.1%
- TPS: > 1000

### 2. 성능 프로파일링

#### JProfiler 활용

- CPU 사용률 분석
- 메모리 할당 패턴 분석
- 메서드 실행 시간 분석

## 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [JPA Best Practices](https://vladmihalcea.com/jpa-persistence-best-practices/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [12-Factor App](https://12factor.net/)
