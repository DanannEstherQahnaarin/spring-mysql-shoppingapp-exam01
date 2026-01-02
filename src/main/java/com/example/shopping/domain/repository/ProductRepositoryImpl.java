package com.example.shopping.domain.repository;

import static com.example.shopping.domain.entity.product.QProduct.product;
import static com.example.shopping.domain.entity.product.QCategory.category;
import java.util.List;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import com.example.shopping.domain.dto.ProdSearchCond;
import com.example.shopping.domain.dto.ProductDto;

import lombok.RequiredArgsConstructor;

/**
 * 상품 Repository 커스텀 구현 클래스
 * 
 * <p>
 * ProductRepositoryC 인터페이스의 구현체로, QueryDSL을 사용하여
 * 복잡한 쿼리를 작성하는 클래스입니다.
 * 
 * <p>
 * 사용 기술:
 * <ul>
 * <li>QueryDSL: 타입 안전한 쿼리 작성</li>
 * <li>JPAQueryFactory: QueryDSL 쿼리 실행을 위한 팩토리</li>
 * <li>Projections: DTO로 결과 매핑</li>
 * </ul>
 * 
 * <p>
 * 주의사항:
 * <ul>
 * <li>클래스명은 반드시 "{Repository명}Impl" 형식이어야 합니다.</li>
 * <li>JPAQueryFactory는 QuerydslConfig에서 빈으로 등록되어 주입됩니다.</li>
 * <li>Q클래스는 컴파일 시 QueryDSL annotation processor에 의해 자동 생성됩니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductCustomRepository {

    /**
     * QueryDSL 쿼리 실행을 위한 팩토리
     * QuerydslConfig에서 빈으로 등록되어 주입됩니다.
     */
    private final JPAQueryFactory queryFactory;

    /**
     * 모든 상품을 카테고리와 조인하여 조회합니다.
     * 
     * <p>
     * 쿼리 동작:
     * <ul>
     * <li>Product와 Category를 조인합니다.</li>
     * <li>ProductDto.Response에 필요한 필드만 선택합니다.</li>
     * <li>상품 ID 기준 내림차순으로 정렬합니다.</li>
     * </ul>
     * 
     * <p>
     * 성능 최적화:
     * <ul>
     * <li>DTO Projection을 사용하여 필요한 필드만 조회합니다.</li>
     * <li>불필요한 필드는 조회하지 않아 네트워크 트래픽과 메모리 사용량을 줄입니다.</li>
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

    @Override
    public Page<ProductDto.Response> search(ProdSearchCond condition, Pageable pageable) {
        // 1. 컨텐츠 조회 쿼리
        List<ProductDto.Response> content = queryFactory
                .select(Projections.fields(ProductDto.Response.class,
                        product.productId,
                        category.name.as("categoryName"),
                        product.name,
                        product.price,
                        product.stock))
                .from(product)
                .leftJoin(product.category, category)
                .where(
                        keywordContains(condition.getKeyword()),
                        categoryIdEq(condition.getCategoryId()),
                        priceBetween(condition.getMinPrice(), condition.getMaxPrice()),
                        inStock(condition.getInStock()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable)) // 동적 정렬
                .fetch();

        // 2. 카운트 쿼리 (페이징 필수)
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .where(
                        keywordContains(condition.getKeyword()),
                        categoryIdEq(condition.getCategoryId()),
                        priceBetween(condition.getMinPrice(), condition.getMaxPrice()),
                        inStock(condition.getInStock()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression keywordContains(String keyword) {
        return StringUtils.hasText(keyword) ? product.name.contains(keyword) : null;
    }

    private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? product.category.categoryId.eq(categoryId) : null;
    }

    private BooleanExpression priceBetween(Integer min, Integer max) {
        if (min == null && max == null)
            return null;
        if (min != null && max == null)
            return product.price.goe(min);
        if (min == null && max != null)
            return product.price.loe(max);
        return product.price.between(min, max);
    }

    private BooleanExpression inStock(Boolean inStock) {
        return (inStock != null && inStock) ? product.stock.gt(0) : null;
    }

    private OrderSpecifier<?> getOrderSpecifier(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "price":
                        return new OrderSpecifier<>(direction, product.price);
                    case "name":
                        return new OrderSpecifier<>(direction, product.name);
                    case "createdAt":
                        return new OrderSpecifier<>(direction, product.productId); // ID가 시간순
                }
            }
        }
        return new OrderSpecifier<>(Order.DESC, product.productId); // 기본 최신순
    }

}
