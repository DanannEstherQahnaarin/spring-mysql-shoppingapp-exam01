package com.example.shopping.domain.entity.order;

import com.example.shopping.domain.entity.product.Product;

import jakarta.persistence.*;
import lombok.*;

/**
 * 장바구니 항목 엔티티
 * 
 * <p>장바구니에 담긴 개별 상품 항목을 나타내는 엔티티입니다.
 * 어떤 상품을 몇 개 담았는지에 대한 정보를 저장합니다.
 * 
 * <p>관계:
 * <ul>
 *   <li>Cart와 N:1 관계를 맺습니다. 하나의 장바구니 항목은 하나의 장바구니에만 속합니다.</li>
 *   <li>Product와 N:1 관계를 맺습니다. 하나의 장바구니 항목은 하나의 상품을 참조합니다.</li>
 * </ul>
 * 
 * <p>제약사항:
 * <ul>
 *   <li>같은 장바구니에 같은 상품은 하나의 항목으로만 존재할 수 있습니다 (unique constraint).</li>
 *   <li>중복 상품 추가 시 새 항목을 생성하지 않고 수량을 증가시켜야 합니다.</li>
 *   <li>수량은 양수 값이어야 합니다.</li>
 * </ul>
 * 
 * <p>비즈니스 로직:
 * <ul>
 *   <li>addQty() 메서드를 통해 기존 항목의 수량을 증가시킬 수 있습니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Entity
@Table(name = "cart_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cart_id", "product_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CartItem {

    /**
     * 장바구니 항목 고유 ID (Primary Key)
     * 데이터베이스에서 자동으로 증가하는 값입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    /**
     * 장바구니 참조
     * 이 항목이 속한 장바구니입니다.
     * LAZY 로딩을 사용하여 필요할 때만 장바구니 정보를 조회합니다.
     * 필수 입력 항목입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    /**
     * 상품 참조
     * 장바구니에 담긴 상품입니다.
     * LAZY 로딩을 사용하여 필요할 때만 상품 정보를 조회합니다.
     * 필수 입력 항목입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * 수량
     * 장바구니에 담은 상품의 수량입니다.
     * 필수 입력 항목이며, 양수 값이어야 합니다.
     */
    @Column(nullable = false)
    private Integer qty;

    /**
     * 수량을 증가시킵니다.
     * 
     * <p>같은 상품을 장바구니에 추가할 때 호출되어
     * 기존 항목의 수량을 증가시키는 메서드입니다.
     * 
     * @param qty 증가시킬 수량 (양수 값이어야 함)
     * 
     * <p>사용 예:
     * <pre>
     * cartItem.addQty(3); // 수량을 3개 증가
     * </pre>
     */
    public void addQty(int qty) {
        this.qty += qty;
    }

    public void updateQty(int qty) {
        this.qty = qty;
    }
}