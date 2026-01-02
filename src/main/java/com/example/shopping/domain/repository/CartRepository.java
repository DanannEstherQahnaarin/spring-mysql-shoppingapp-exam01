package com.example.shopping.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopping.domain.entity.order.Cart;

/**
 * 장바구니 Repository 인터페이스
 * 
 * <p>Cart 엔티티에 대한 데이터 접근을 제공하는 Repository 인터페이스입니다.
 * Spring Data JPA의 JpaRepository를 상속받아 기본 CRUD 메서드를 제공하며,
 * 사용자 ID로 장바구니를 조회하는 메서드를 제공합니다.
 * 
 * <p>제공 메서드:
 * <ul>
 *   <li>JpaRepository 기본 메서드: save, findById, findAll, delete 등</li>
 *   <li>findByUserId: 사용자 ID로 장바구니 조회</li>
 * </ul>
 * 
 * <p>사용 예:
 * <pre>
 * Optional&lt;Cart&gt; cart = cartRepository.findByUserId(userId);
 * Cart newCart = cartRepository.save(Cart.builder().userId(userId).build());
 * </pre>
 * 
 * @author shopping-server
 * @since 1.0
 */
public interface CartRepository extends JpaRepository<Cart, Long>, CartCustomRepository {
    /**
     * 사용자 ID로 장바구니를 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @return 장바구니 엔티티 (Optional) - 사용자당 하나의 장바구니만 존재하므로 최대 1개
     */
    Optional<Cart> findByUserId(Long userId);
}
