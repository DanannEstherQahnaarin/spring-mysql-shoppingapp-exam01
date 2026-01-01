package com.example.shopping.domain.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopping.domain.dto.ProductDto;
import com.example.shopping.domain.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 상품 컨트롤러
 * 
 * <p>상품과 카테고리 관련 REST API 엔드포인트를 제공하는 컨트롤러입니다.
 * 카테고리 생성, 상품 등록, 상품 목록 조회 등의 기능을 처리합니다.
 * 
 * <p>API 경로:
 * <ul>
 *   <li>기본 경로: /api/products</li>
 * </ul>
 * 
 * <p>제공 엔드포인트:
 * <ul>
 *   <li>POST /api/products/category/add: 카테고리 등록</li>
 *   <li>POST /api/products/add: 상품 등록</li>
 *   <li>GET /api/products/list: 상품 목록 조회</li>
 * </ul>
 * 
 * <p>데이터 검증:
 * <ul>
 *   <li>모든 요청 DTO는 @Valid 어노테이션으로 검증됩니다.</li>
 *   <li>Jakarta Validation을 사용하여 필수 필드, 최소값 등을 검증합니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    /** 상품 서비스 */
    private final ProductService productService;

    /**
     * 카테고리 등록 API
     * 
     * <p>새로운 상품 카테고리를 등록합니다.
     * 
     * <p>요청:
     * <ul>
     *   <li>Content-Type: application/json</li>
     *   <li>Body: ProductDto.CreateCategory (카테고리 이름)</li>
     * </ul>
     * 
     * <p>응답:
     * <ul>
     *   <li>Status: 200 OK</li>
     *   <li>Body: 생성된 카테고리 ID (Long)</li>
     * </ul>
     * 
     * <p>검증:
     * <ul>
     *   <li>카테고리 이름은 필수 입력 항목입니다.</li>
     *   <li>카테고리 이름은 고유해야 합니다 (중복 시 데이터베이스 예외 발생).</li>
     * </ul>
     * 
     * @param request 카테고리 생성 요청 DTO
     * @return 생성된 카테고리 ID를 포함한 ResponseEntity
     */
    @PostMapping("/category/add")
    public ResponseEntity<Long> createCategory(@RequestBody @Valid ProductDto.CreateCategory request) {
        Long id = productService.createCategory(request);
        return ResponseEntity.ok(id);
    }

    /**
     * 상품 등록 API
     * 
     * <p>새로운 상품을 등록합니다.
     * 
     * <p>요청:
     * <ul>
     *   <li>Content-Type: application/json</li>
     *   <li>Body: ProductDto.CreateProduct (카테고리 ID, 상품명, 가격, 재고)</li>
     * </ul>
     * 
     * <p>응답:
     * <ul>
     *   <li>Status: 200 OK</li>
     *   <li>Body: 생성된 상품 ID (Long)</li>
     * </ul>
     * 
     * <p>검증:
     * <ul>
     *   <li>카테고리 ID, 상품명, 가격, 재고는 필수 입력 항목입니다.</li>
     *   <li>가격과 재고는 0 이상의 값이어야 합니다.</li>
     *   <li>카테고리가 존재하지 않으면 예외가 발생합니다.</li>
     * </ul>
     * 
     * @param request 상품 생성 요청 DTO
     * @return 생성된 상품 ID를 포함한 ResponseEntity
     */
    @PostMapping("/add")
    public ResponseEntity<Long> createProduct(@RequestBody @Valid ProductDto.CreateProduct request) {
        Long id = productService.createProduct(request);
        return ResponseEntity.ok(id);
    }

    /**
     * 상품 목록 조회 API
     * 
     * <p>모든 상품 목록을 조회합니다.
     * QueryDSL을 사용하여 카테고리 정보와 함께 조회합니다.
     * 
     * <p>요청:
     * <ul>
     *   <li>Method: GET</li>
     *   <li>인증: 현재 버전에서는 불필요 (향후 인증 추가 가능)</li>
     * </ul>
     * 
     * <p>응답:
     * <ul>
     *   <li>Status: 200 OK</li>
     *   <li>Body: ProductDto.Response 리스트 (상품 ID, 카테고리 이름, 상품명, 가격, 재고)</li>
     *   <li>정렬: 상품 ID 내림차순</li>
     * </ul>
     * 
     * <p>성능:
     * <ul>
     *   <li>DTO Projection을 사용하여 필요한 필드만 조회합니다.</li>
     *   <li>읽기 전용 트랜잭션을 사용하여 성능을 최적화합니다.</li>
     * </ul>
     * 
     * @return 상품 목록을 포함한 ResponseEntity
     */
    @GetMapping("/list")
    public ResponseEntity<java.util.List<ProductDto.Response>> getProductList() {
        return ResponseEntity.ok(productService.getProductList());
    }
}
