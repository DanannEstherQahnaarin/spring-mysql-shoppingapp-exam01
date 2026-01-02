package com.example.shopping.domain.entity.product;

import com.example.shopping.domain.exception.BusinessException;
import com.example.shopping.domain.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

/**
 * 상품 엔티티
 * 
 * <p>쇼핑몰에서 판매하는 상품의 정보를 관리하는 엔티티입니다.
 * 상품명, 가격, 재고 수량 등의 정보를 저장하며, 카테고리와 연결됩니다.
 * 
 * <p>관계:
 * <ul>
 *   <li>Category와 N:1 관계를 맺습니다. 하나의 상품은 하나의 카테고리에만 속할 수 있습니다.</li>
 *   <li>CartItem과 1:N 관계를 맺습니다. 하나의 상품이 여러 장바구니 항목에 포함될 수 있습니다.</li>
 *   <li>OrderItem과 1:N 관계를 맺습니다. 하나의 상품이 여러 주문 항목에 포함될 수 있습니다.</li>
 * </ul>
 * 
 * <p>비즈니스 로직:
 * <ul>
 *   <li>주문 시 재고를 차감하는 removeStock() 메서드를 제공합니다.</li>
 *   <li>재고 부족 시 예외를 발생시켜 트랜잭션 롤백을 유도합니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {
    
    /**
     * 상품 고유 ID (Primary Key)
     * 데이터베이스에서 자동으로 증가하는 값입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    /**
     * 상품 카테고리
     * 이 상품이 속한 카테고리입니다.
     * LAZY 로딩을 사용하여 필요할 때만 카테고리 정보를 조회합니다.
     * 필수 입력 항목입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * 상품명
     * 상품의 이름입니다. 필수 입력 항목이며, 최대 100자까지 입력 가능합니다.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 상품 가격
     * 상품의 판매 가격입니다. 단위는 원(KRW)입니다.
     * 필수 입력 항목이며, 0 이상의 값이어야 합니다.
     */
    @Column(nullable = false)
    private Integer price;

    /**
     * 재고 수량
     * 현재 상품의 재고 수량입니다.
     * 주문 시 이 값이 차감되며, 0 미만이 될 수 없습니다.
     * 필수 입력 항목이며, 0 이상의 값이어야 합니다.
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * 재고를 차감합니다.
     * 
     * <p>주문 생성 시 호출되어 상품의 재고를 차감하는 메서드입니다.
     * 재고가 부족한 경우(차감 후 음수가 되는 경우) 예외를 발생시켜
     * 트랜잭션을 롤백합니다.
     * 
     * @param quantity 차감할 수량 (양수 값이어야 함)
     * @throws RuntimeException 재고가 부족한 경우 발생
     * 
     * <p>사용 예:
     * <pre>
     * product.removeStock(5); // 재고를 5개 차감
     * </pre>
     */
    public void removeStock(int quantity) {
        int restStock = this.stock - quantity;
        if (restStock < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
        this.stock = restStock;
    }

    public void addStock(int quantity) {
        this.stock += quantity;
    }
}
