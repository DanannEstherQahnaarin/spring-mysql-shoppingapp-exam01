package com.example.shopping.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.shopping.domain.dto.ProdSearchCond;
import com.example.shopping.domain.dto.ProductDto;

/**
 * 상품 Repository 커스텀 인터페이스
 * 
 * <p>ProductRepository에서 사용할 커스텀 쿼리 메서드를 정의하는 인터페이스입니다.
 * QueryDSL을 사용한 복잡한 쿼리를 작성하기 위해 사용됩니다.
 * 
 * <p>구현 클래스:
 * <ul>
 *   <li>ProductRepositoryImpl: 이 인터페이스의 구현체로, QueryDSL을 사용하여 실제 쿼리를 작성합니다.</li>
 * </ul>
 * 
 * <p>설계 목적:
 * <ul>
 *   <li>Spring Data JPA의 기본 메서드로는 처리하기 어려운 복잡한 조인 쿼리를 작성합니다.</li>
 *   <li>DTO Projection을 사용하여 필요한 필드만 조회하여 성능을 최적화합니다.</li>
 *   <li>타입 안전한 쿼리 작성이 가능합니다.</li>
 * </ul>
 * 
 * <p>사용 예:
 * <pre>
 * List&lt;ProductDto.Response&gt; products = productRepository.findAllProducts();
 * </pre>
 * 
 * @author shopping-server
 * @since 1.0
 */
public interface ProductCustomRepository {
    List<ProductDto.Response> findAllProducts();

    Page<ProductDto.Response> search(ProdSearchCond condition, Pageable pageable);
}
