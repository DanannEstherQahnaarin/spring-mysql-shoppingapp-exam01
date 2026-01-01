package com.example.shopping.domain.repository;

import static com.example.shopping.domain.entity.product.QProduct.product;
import static com.example.shopping.domain.entity.product.QCategory.category;
import java.util.List;

import com.example.shopping.domain.dto.ProductDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * 상품 Repository 커스텀 구현 클래스
 * 
 * <p>ProductRepositoryC 인터페이스의 구현체로, QueryDSL을 사용하여
 * 복잡한 쿼리를 작성하는 클래스입니다.
 * 
 * <p>사용 기술:
 * <ul>
 *   <li>QueryDSL: 타입 안전한 쿼리 작성</li>
 *   <li>JPAQueryFactory: QueryDSL 쿼리 실행을 위한 팩토리</li>
 *   <li>Projections: DTO로 결과 매핑</li>
 * </ul>
 * 
 * <p>주의사항:
 * <ul>
 *   <li>클래스명은 반드시 "{Repository명}Impl" 형식이어야 합니다.</li>
 *   <li>JPAQueryFactory는 QuerydslConfig에서 빈으로 등록되어 주입됩니다.</li>
 *   <li>Q클래스는 컴파일 시 QueryDSL annotation processor에 의해 자동 생성됩니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryC {

    /**
     * QueryDSL 쿼리 실행을 위한 팩토리
     * QuerydslConfig에서 빈으로 등록되어 주입됩니다.
     */
    private final JPAQueryFactory queryFactory;

    /**
     * 모든 상품을 카테고리와 조인하여 조회합니다.
     * 
     * <p>쿼리 동작:
     * <ul>
     *   <li>Product와 Category를 조인합니다.</li>
     *   <li>ProductDto.Response에 필요한 필드만 선택합니다.</li>
     *   <li>상품 ID 기준 내림차순으로 정렬합니다.</li>
     * </ul>
     * 
     * <p>성능 최적화:
     * <ul>
     *   <li>DTO Projection을 사용하여 필요한 필드만 조회합니다.</li>
     *   <li>불필요한 필드는 조회하지 않아 네트워크 트래픽과 메모리 사용량을 줄입니다.</li>
     * </ul>
     * 
     * @return 상품 목록 (DTO 리스트), 상품 ID 내림차순 정렬
     */
    @Override
    public List<ProductDto.Response> findAllProducts() {
        return queryFactory
                .select(Projections.fields(ProductDto.Response.class,
                        product.productId,
                        category.name.as("categoryName"),
                        product.name,
                        product.price,
                        product.stock))
                .from(product)
                .join(product.category, category)
                .orderBy(product.productId.desc())
                .fetch();
    }

}
