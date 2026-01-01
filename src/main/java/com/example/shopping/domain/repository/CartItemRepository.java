package com.example.shopping.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopping.domain.entity.order.CartItem;

/**
 * 장바구니 항목 Repository 인터페이스
 * 
 * <p>CartItem 엔티티에 대한 데이터 접근을 제공하는 Repository 인터페이스입니다.
 * Spring Data JPA의 JpaRepository를 상속받아 기본 CRUD 메서드를 제공하며,
 * 장바구니 관련 커스텀 쿼리 메서드를 제공합니다.
 * 
 * <p>제공 메서드:
 * <ul>
 *   <li>JpaRepository 기본 메서드: save, findById, findAll, delete 등</li>
 *   <li>findByCart_CartIdAndProduct_ProductId: 장바구니 ID와 상품 ID로 항목 조회</li>
 *   <li>findAllByCart_CartId: 장바구니의 모든 항목 조회</li>
 *   <li>deleteAllByCart_CartId: 장바구니의 모든 항목 삭제</li>
 * </ul>
 * 
 * <p>사용 예:
 * <pre>
 * Optional&lt;CartItem&gt; cartItem = cartItemRepository
 *     .findByCart_CartIdAndProduct_ProductId(cartId, productId);
 * List&lt;CartItem&gt; items = cartItemRepository.findAllByCart_CartId(cartId);
 * cartItemRepository.deleteAllByCart_CartId(cartId);
 * </pre>
 * 
 * @author shopping-server
 * @since 1.0
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /**
     * 장바구니 ID와 상품 ID로 장바구니 항목을 조회합니다.
     * 
     * <p>같은 상품이 장바구니에 이미 있는지 확인할 때 사용합니다.
     * unique constraint에 의해 최대 1개의 항목만 반환됩니다.
     * 
     * @param cartId 장바구니 ID
     * @param productId 상품 ID
     * @return 장바구니 항목 엔티티 (Optional)
     */
    Optional<CartItem> findByCart_CartIdAndProduct_ProductId(Long cartId, Long productId);
    
    /**
     * 장바구니의 모든 항목을 조회합니다.
     * 
     * @param cartId 조회할 장바구니의 ID
     * @return 장바구니 항목 리스트
     */
    List<CartItem> findAllByCart_CartId(Long cartId);
    
    /**
     * 장바구니의 모든 항목을 삭제합니다.
     * 
     * <p>주문 완료 후 장바구니를 비울 때 사용합니다.
     * 
     * @param cartId 삭제할 장바구니의 ID
     */
    void deleteAllByCart_CartId(Long cartId);
}