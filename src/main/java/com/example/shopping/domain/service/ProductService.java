package com.example.shopping.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.shopping.domain.dto.ProductDto;
import com.example.shopping.domain.exception.BusinessException;
import com.example.shopping.domain.exception.ErrorCode;
import com.example.shopping.domain.entity.product.Category;
import com.example.shopping.domain.entity.product.Product;
import com.example.shopping.domain.repository.CategoryRepository;
import com.example.shopping.domain.repository.ProductRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * 상품 서비스 클래스
 * 
 * <p>상품과 카테고리 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 카테고리 생성, 상품 등록, 상품 목록 조회 등의 기능을 제공합니다.
 * 
 * <p>주요 기능:
 * <ul>
 *   <li>카테고리 생성: 새로운 상품 카테고리를 등록합니다.</li>
 *   <li>상품 등록: 카테고리에 속한 상품을 등록합니다.</li>
 *   <li>상품 목록 조회: QueryDSL을 사용하여 상품 목록을 조회합니다.</li>
 * </ul>
 * 
 * <p>트랜잭션 관리:
 * <ul>
 *   <li>createCategory, createProduct: 쓰기 작업이므로 @Transactional 사용</li>
 *   <li>getProductList: 읽기 전용이므로 @Transactional(readOnly = true) 사용하여 성능 최적화</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    /** 카테고리 Repository */
    private final CategoryRepository categoryRepository;
    
    /** 상품 Repository */
    private final ProductRepository productRepository;

    /**
     * 새로운 카테고리를 생성합니다.
     * 
     * <p>처리 과정:
     * <ol>
     *   <li>요청에서 카테고리 이름을 가져옵니다.</li>
     *   <li>Category 엔티티를 생성합니다.</li>
     *   <li>데이터베이스에 저장하고 생성된 카테고리 ID를 반환합니다.</li>
     * </ol>
     * 
     * <p>제약사항:
     * <ul>
     *   <li>카테고리 이름은 고유해야 합니다 (데이터베이스 제약조건).</li>
     *   <li>카테고리 이름은 필수 입력 항목입니다.</li>
     * </ul>
     * 
     * @param request 카테고리 생성 요청 DTO (카테고리 이름)
     * @return 생성된 카테고리의 ID
     */
    @Transactional
    public Long createCategory(ProductDto.CreateCategory request) {
        Category category = Category.builder()
                .name(request.getName())
                .build();

        return categoryRepository.save(category).getCategoryId();
    }

    /**
     * 새로운 상품을 등록합니다.
     * 
     * <p>처리 과정:
     * <ol>
     *   <li>요청에서 카테고리 ID로 카테고리를 조회합니다.</li>
     *   <li>요청 정보를 바탕으로 Product 엔티티를 생성합니다.</li>
     *   <li>데이터베이스에 저장하고 생성된 상품 ID를 반환합니다.</li>
     * </ol>
     * 
     * <p>검증:
     * <ul>
     *   <li>카테고리가 존재하지 않으면 예외가 발생합니다.</li>
     *   <li>가격과 재고는 0 이상의 값이어야 합니다 (DTO 검증).</li>
     * </ul>
     * 
     * @param request 상품 생성 요청 DTO (카테고리 ID, 상품명, 가격, 재고)
     * @return 생성된 상품의 ID
     * @throws RuntimeException 카테고리가 존재하지 않는 경우
     */
    @Transactional
    public Long createProduct(ProductDto.CreateProduct request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .category(category)
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();

        return productRepository.save(product).getProductId();
    }

    /**
     * 모든 상품 목록을 조회합니다.
     * 
     * <p>조회 방식:
     * <ul>
     *   <li>QueryDSL을 사용하여 Product와 Category를 조인합니다.</li>
     *   <li>DTO Projection을 사용하여 필요한 필드만 조회합니다.</li>
     *   <li>상품 ID 기준 내림차순으로 정렬합니다.</li>
     * </ul>
     * 
     * <p>성능 최적화:
     * <ul>
     *   <li>@Transactional(readOnly = true)를 사용하여 읽기 전용 트랜잭션으로 설정합니다.</li>
     *   <li>읽기 전용 트랜잭션은 플러시를 생략하고 성능을 향상시킵니다.</li>
     *   <li>DTO Projection을 통해 불필요한 필드를 조회하지 않습니다.</li>
     * </ul>
     * 
     * @return 상품 목록 (DTO 리스트), 상품 ID 내림차순 정렬
     */
    @Transactional(readOnly = true)
    public java.util.List<ProductDto.Response> getProductList() {
        return productRepository.findAllProducts();
    }

    // 상품 상세 조회
    @Transactional(readOnly = true)
    public ProductDto.Response getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductDto.Response res = new ProductDto.Response();
        
        res.setProductId(product.getProductId());
        res.setCategoryName(product.getCategory().getName());
        res.setName(product.getName());
        res.setPrice(product.getPrice());
        res.setStock(product.getStock());
        return res;
    }

    // 상품 수정 (관리자)
    @Transactional
    public void updateProduct(Long productId, ProductDto.UpdateProduct request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // Dirty Checking을 이용한 업데이트 (Setter 또는 별도 메서드 필요)
        // 편의상 Setter 사용 혹은 엔티티에 update 메서드 추가 권장
        // product.updateInfo(category, request.getName(), request.getPrice(), request.getStock()); 
        // 여기서는 Builder 패턴 사용이 불가능하므로 엔티티에 메서드 추가 필요 *아래 2-1 참고
        product.updateInfo(category, request.getName(), request.getPrice(), request.getStock());
    }

    // 상품 삭제 (관리자)
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(product);
    }

    // =========================================================
    // 카테고리 관리 (Category)
    // =========================================================

    // 카테고리 목록 조회
    @Transactional(readOnly = true)
    public List<ProductDto.CategoryResponse> getCategoryList() {
        return categoryRepository.findAll().stream()
                .map(c -> new ProductDto.CategoryResponse(c.getCategoryId(), c.getName()))
                .collect(Collectors.toList());
    }

    // 카테고리 수정
    @Transactional
    public void updateCategory(Long categoryId, ProductDto.UpdateCategory request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        category.updateName(request.getName());
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        // 상품이 연결된 경우 삭제 방지 로직 필요하나 여기선 생략
        categoryRepository.delete(category);
    }
}
