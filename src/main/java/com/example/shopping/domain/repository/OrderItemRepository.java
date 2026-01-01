package com.example.shopping.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopping.domain.entity.order.OrderItem;

/**
 * 주문 항목 Repository 인터페이스
 * 
 * <p>OrderItem 엔티티에 대한 데이터 접근을 제공하는 Repository 인터페이스입니다.
 * Spring Data JPA의 JpaRepository를 상속받아 기본 CRUD 메서드를 제공합니다.
 * 
 * <p>제공 메서드:
 * <ul>
 *   <li>JpaRepository 기본 메서드: save, findById, findAll, delete 등</li>
 * </ul>
 * 
 * <p>사용 예:
 * <pre>
 * OrderItem orderItem = OrderItem.builder()
 *     .order(order)
 *     .product(product)
 *     .qty(quantity)
 *     .priceAtOrder(product.getPrice())
 *     .build();
 * orderItemRepository.save(orderItem);
 * </pre>
 * 
 * @author shopping-server
 * @since 1.0
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}