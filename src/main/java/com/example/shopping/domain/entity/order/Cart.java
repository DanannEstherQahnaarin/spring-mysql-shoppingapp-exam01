package com.example.shopping.domain.entity.order;

import jakarta.persistence.*;
import lombok.*;

/**
 * 장바구니 엔티티
 * 
 * <p>사용자가 구매하기 전 상품을 담아두는 장바구니를 관리하는 엔티티입니다.
 * 각 사용자는 하나의 장바구니를 가지며, 장바구니에는 여러 상품을 담을 수 있습니다.
 * 
 * <p>관계:
 * <ul>
 *   <li>CartItem과 1:N 관계를 맺습니다. 하나의 장바구니에 여러 장바구니 항목이 포함될 수 있습니다.</li>
 * </ul>
 * 
 * <p>설계 특징:
 * <ul>
 *   <li>User 엔티티와 직접적인 JPA 관계를 맺지 않고, userId를 Long 타입으로 저장합니다.</li>
 *   <li>이러한 설계는 User 엔티티의 변경이 Cart에 영향을 주지 않도록 하는 목적입니다.</li>
 *   <li>userId는 고유해야 하며, 사용자당 하나의 장바구니만 생성됩니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Entity
@Table(name = "cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Cart {

    /**
     * 장바구니 고유 ID (Primary Key)
     * 데이터베이스에서 자동으로 증가하는 값입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    /**
     * 사용자 ID
     * 이 장바구니를 소유한 사용자의 ID입니다.
     * 고유해야 하며, 사용자당 하나의 장바구니만 존재할 수 있습니다.
     * 필수 입력 항목입니다.
     * 
     * <p>주의: User 엔티티와 직접적인 JPA 관계를 맺지 않고 ID만 저장합니다.
     */
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;
}