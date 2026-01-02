package com.example.shopping.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopping.domain.entity.product.Product;

/**
 * 상품 Repository 인터페이스
 * 
 * <p>Product 엔티티에 대한 데이터 접근을 제공하는 Repository 인터페이스입니다.
 * Spring Data JPA의 JpaRepository를 상속받아 기본 CRUD 메서드를 제공하며,
 * ProductRepositoryC 커스텀 인터페이스를 상속받아 QueryDSL 기반 커스텀 쿼리를 사용합니다.
 * 
 * <p>제공 메서드:
 * <ul>
 *   <li>JpaRepository 기본 메서드: save, findById, findAll, delete 등</li>
 *   <li>ProductRepositoryC의 findAllProducts: QueryDSL을 사용한 상품 목록 조회</li>
 * </ul>
 * 
 * <p>설계 특징:
 * <ul>
 *   <li>QueryDSL을 사용한 동적 쿼리 작성을 위해 커스텀 Repository 패턴을 사용합니다.</li>
 *   <li>ProductRepositoryC 인터페이스와 ProductRepositoryImpl 구현체로 분리되어 있습니다.</li>
 * </ul>
 * 
 * <p>사용 예:
 * <pre>
 * Product product = productRepository.findById(productId)
 *     .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
 * List&lt;ProductDto.Response&gt; products = productRepository.findAllProducts();
 * </pre>
 * 
 * @author shopping-server
 * @since 1.0
 */
public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {
    
}
