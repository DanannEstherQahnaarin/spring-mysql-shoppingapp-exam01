package com.example.shopping.domain.entity.order;

import com.example.shopping.domain.entity.product.Product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 항목 엔티티
 * 
 * <p>하나의 주문에 포함된 개별 상품 항목을 나타내는 엔티티입니다.
 * 주문 시점의 상품 가격과 수량을 저장하여, 이후 상품 가격이 변경되어도
 * 주문 당시의 가격을 추적할 수 있도록 합니다.
 * 
 * <p>관계:
 * <ul>
 *   <li>Orders와 N:1 관계를 맺습니다. 하나의 주문 항목은 하나의 주문에만 속합니다.</li>
 *   <li>Product와 N:1 관계를 맺습니다. 하나의 주문 항목은 하나의 상품을 참조합니다.</li>
 * </ul>
 * 
 * <p>설계 목적:
 * <ul>
 *   <li>주문 시점의 상품 가격을 priceAtOrder 필드에 저장하여 가격 변동에 영향받지 않습니다.</li>
 *   <li>상품 가격이 변경되어도 과거 주문 내역의 정확성을 유지할 수 있습니다.</li>
 *   <li>주문 항목별 수량을 추적하여 주문 상세 정보를 제공합니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Entity
@Table(name = "order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderItem {

    /**
     * 주문 항목 고유 ID (Primary Key)
     * 데이터베이스에서 자동으로 증가하는 값입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    /**
     * 주문 참조
     * 이 항목이 속한 주문입니다.
     * LAZY 로딩을 사용하여 필요할 때만 주문 정보를 조회합니다.
     * 필수 입력 항목입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    /**
     * 상품 참조
     * 주문한 상품입니다.
     * LAZY 로딩을 사용하여 필요할 때만 상품 정보를 조회합니다.
     * 필수 입력 항목입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * 주문 수량
     * 주문한 상품의 수량입니다.
     * 필수 입력 항목이며, 양수 값이어야 합니다.
     */
    @Column(nullable = false)
    private Integer qty;

    /**
     * 주문 시점의 상품 가격
     * 주문이 생성된 시점의 상품 가격을 저장합니다.
     * 이후 상품 가격이 변경되어도 주문 당시의 가격을 추적할 수 있습니다.
     * 필수 입력 항목이며, 단위는 원(KRW)입니다.
     */
    @Column(name = "price_at_order", nullable = false)
    private Integer priceAtOrder;
}