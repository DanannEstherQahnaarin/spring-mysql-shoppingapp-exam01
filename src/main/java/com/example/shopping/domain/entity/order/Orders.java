package com.example.shopping.domain.entity.order;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.shopping.domain.entity.BaseTimeEntity;

/**
 * 주문 엔티티
 * 
 * <p>사용자가 상품을 주문할 때 생성되는 주문 정보를 관리하는 엔티티입니다.
 * 주문 ID, 주문한 사용자, 주문 상태, 주문 시간 등의 정보를 저장합니다.
 * 
 * <p>관계:
 * <ul>
 *   <li>OrderItem과 1:N 관계를 맺습니다. 하나의 주문에 여러 주문 항목이 포함될 수 있습니다.</li>
 * </ul>
 * 
 * <p>상속:
 * <ul>
 *   <li>BaseTimeEntity를 상속받아 createdAt과 updatedAt 필드를 자동으로 관리합니다.</li>
 * </ul>
 * 
 * <p>주문 상태:
 * <ul>
 *   <li>"complete": 주문 완료 (기본값)</li>
 *   <li>"cancel": 주문 취소</li>
 *   <li>향후 "pending", "shipping", "delivered" 등 추가 가능</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Orders extends BaseTimeEntity {

    /**
     * 주문 고유 ID (Primary Key)
     * 데이터베이스에서 자동으로 증가하는 값입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 사용자 ID
     * 이 주문을 한 사용자의 ID입니다.
     * 필수 입력 항목입니다.
     * 
     * <p>주의: User 엔티티와 직접적인 JPA 관계를 맺지 않고 ID만 저장합니다.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 주문 상태
     * 주문의 현재 상태를 나타내는 문자열입니다.
     * 기본값은 "complete"이며, 필수 입력 항목입니다.
     * 
     * <p>가능한 값:
     * <ul>
     *   <li>"complete": 주문 완료</li>
     *   <li>"cancel": 주문 취소</li>
     * </ul>
     */
    @Column(nullable = false)
    @Builder.Default
    private String status = "complete";

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * 주문 시간
     * 주문이 생성된 시간입니다.
     * 기본값은 현재 시간이며, 주문 생성 시 자동으로 설정됩니다.
     */
    @Column(name = "ordered_at")
    @Builder.Default
    private LocalDateTime orderedAt = LocalDateTime.now();
}