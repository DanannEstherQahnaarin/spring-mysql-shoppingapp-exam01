package com.example.shopping.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopping.domain.dto.OrderDto;
import com.example.shopping.domain.exception.BusinessException;
import com.example.shopping.domain.exception.ErrorCode;
import com.example.shopping.domain.entity.order.Cart;
import com.example.shopping.domain.entity.order.CartItem;
import com.example.shopping.domain.entity.order.OrderItem;
import com.example.shopping.domain.entity.order.Orders;
import com.example.shopping.domain.entity.product.Product;
import com.example.shopping.domain.repository.CartItemRepository;
import com.example.shopping.domain.repository.CartRepository;
import com.example.shopping.domain.repository.OrderItemRepository;
import com.example.shopping.domain.repository.OrdersRepository;
import com.example.shopping.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

/**
 * 주문 서비스 클래스
 * 
 * <p>주문과 장바구니 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 장바구니 담기, 장바구니 조회, 주문하기 등의 기능을 제공합니다.
 * 
 * <p>주요 기능:
 * <ul>
 *   <li>장바구니 담기: 상품을 장바구니에 추가 (중복 시 수량 증가)</li>
 *   <li>장바구니 조회: 사용자의 장바구니 항목 목록 조회</li>
 *   <li>주문하기: 장바구니의 모든 상품을 주문 처리 (재고 차감, 주문 내역 저장, 장바구니 비우기)</li>
 * </ul>
 * 
 * <p>트랜잭션 관리:
 * <ul>
 *   <li>addToCart, createOrder: 쓰기 작업이므로 @Transactional 사용</li>
 *   <li>getCartItems: 읽기 전용이므로 @Transactional(readOnly = true) 사용</li>
 *   <li>createOrder는 여러 테이블을 수정하므로 원자성이 매우 중요합니다.</li>
 * </ul>
 * 
 * <p>데이터 무결성:
 * <ul>
 *   <li>주문 시 재고 차감과 주문 내역 저장이 하나의 트랜잭션으로 처리됩니다.</li>
 *   <li>재고 부족 시 예외가 발생하여 전체 트랜잭션이 롤백됩니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    /** 장바구니 Repository */
    private final CartRepository cartRepository;
    
    /** 장바구니 항목 Repository */
    private final CartItemRepository cartItemRepository;
    
    /** 상품 Repository */
    private final ProductRepository productRepository;
    
    /** 주문 Repository */
    private final OrdersRepository ordersRepository;
    
    /** 주문 항목 Repository */
    private final OrderItemRepository orderItemRepository;

    /**
     * 장바구니에 상품을 담습니다.
     * 
     * <p>처리 과정:
     * <ol>
     *   <li>사용자의 장바구니를 조회합니다 (없으면 생성).</li>
     *   <li>요청된 상품이 존재하는지 확인합니다.</li>
     *   <li>장바구니에 같은 상품이 이미 있는지 확인합니다.</li>
     *   <li>이미 있으면 수량을 증가시키고, 없으면 새로운 항목을 생성합니다.</li>
     * </ol>
     * 
     * <p>비즈니스 로직:
     * <ul>
     *   <li>같은 상품은 장바구니에 하나의 항목으로만 존재합니다 (unique constraint).</li>
     *   <li>중복 추가 시 새 항목을 생성하지 않고 기존 항목의 수량을 증가시킵니다.</li>
     * </ul>
     * 
     * <p>트랜잭션:
     * <ul>
     *   <li>장바구니 생성, 항목 추가/수정이 하나의 트랜잭션으로 처리됩니다.</li>
     * </ul>
     * 
     * @param userId 사용자 ID
     * @param request 장바구니 담기 요청 DTO (상품 ID, 수량)
     * @throws RuntimeException 상품이 존재하지 않는 경우
     */
    @Transactional
    public void addToCart(Long userId, OrderDto.AddToCart request) {
        // 1. 유저의 장바구니 조회 (없으면 생성)
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        // 2. 이미 장바구니에 있는 상품인지 확인
        CartItem cartItem = cartItemRepository
                .findByCart_CartIdAndProduct_ProductId(cart.getCartId(), product.getProductId())
                .orElse(null);

        if (cartItem != null) {
            // 이미 있으면 수량 증가
            cartItem.addQty(request.getQty());
        } else {
            // 없으면 새로 생성
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .qty(request.getQty())
                    .build();
            cartItemRepository.save(cartItem);
        }
    }

    /**
     * 사용자의 장바구니 항목 목록을 조회합니다.
     * 
     * <p>처리 과정:
     * <ol>
     *   <li>사용자의 장바구니를 조회합니다.</li>
     *   <li>장바구니가 없으면 빈 리스트를 반환합니다.</li>
     *   <li>장바구니의 모든 항목을 조회합니다.</li>
     *   <li>각 항목을 DTO로 변환하여 반환합니다.</li>
     * </ol>
     * 
     * <p>DTO 변환:
     * <ul>
     *   <li>CartItem 엔티티를 OrderDto.CartItemResponse DTO로 변환합니다.</li>
     *   <li>상품 정보(이름, 가격)를 포함하여 반환합니다.</li>
     * </ul>
     * 
     * <p>성능 최적화:
     * <ul>
     *   <li>@Transactional(readOnly = true)를 사용하여 읽기 전용 트랜잭션으로 설정합니다.</li>
     * </ul>
     * 
     * @param userId 사용자 ID
     * @return 장바구니 항목 목록 (DTO 리스트), 장바구니가 없으면 빈 리스트
     */
    @Transactional(readOnly = true)
    public List<OrderDto.CartItemResponse> getCartItems(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null)
            return List.of();

        return cartItemRepository.findAllByCart_CartId(cart.getCartId()).stream()
                .map(item -> {
                    OrderDto.CartItemResponse res = new OrderDto.CartItemResponse();
                    res.setCartItemId(item.getCartItemId());
                    res.setProductId(item.getProduct().getProductId());
                    res.setProductName(item.getProduct().getName());
                    res.setPrice(item.getProduct().getPrice());
                    res.setQty(item.getQty());
                    return res;
                }).collect(Collectors.toList());
    }

    /**
     * 장바구니의 모든 상품을 주문 처리합니다.
     * 
     * <p>처리 과정:
     * <ol>
     *   <li>사용자의 장바구니를 조회합니다.</li>
     *   <li>장바구니의 모든 항목을 조회합니다.</li>
     *   <li>주문 엔티티를 생성하고 저장합니다.</li>
     *   <li>각 장바구니 항목에 대해:
     *     <ul>
     *       <li>상품의 재고를 차감합니다 (재고 부족 시 예외 발생).</li>
     *       <li>주문 항목을 생성하고 저장합니다 (주문 시점의 가격 저장).</li>
     *     </ul>
     *   </li>
     *   <li>장바구니의 모든 항목을 삭제합니다.</li>
     * </ol>
     * 
     * <p>트랜잭션 중요성:
     * <ul>
     *   <li>모든 작업이 하나의 트랜잭션으로 처리되어 원자성이 보장됩니다.</li>
     *   <li>재고 차감 실패 시 전체 주문이 롤백되어 데이터 무결성이 유지됩니다.</li>
     *   <li>주문 생성 후 재고 차감 실패 시에도 주문이 롤백됩니다.</li>
     * </ul>
     * 
     * <p>데이터 무결성:
     * <ul>
     *   <li>재고 부족 시 RuntimeException이 발생하여 트랜잭션이 롤백됩니다.</li>
     *   <li>주문 시점의 상품 가격을 priceAtOrder에 저장하여 가격 변동에 영향받지 않습니다.</li>
     * </ul>
     * 
     * @param userId 사용자 ID
     * @return 생성된 주문의 ID
     * @throws RuntimeException 장바구니가 비어있거나, 주문할 상품이 없거나, 재고가 부족한 경우
     */
    @Transactional
    public Long createOrder(Long userId) {
        // 1. 장바구니 조회
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_EMPTY));

        List<CartItem> cartItems = cartItemRepository.findAllByCart_CartId(cart.getCartId());
        if (cartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.NO_ITEMS_TO_ORDER);
        }

        // 2. 주문 생성
        Orders order = Orders.builder()
                .userId(userId)
                .status("complete")
                .build();
        ordersRepository.save(order);

        // 3. 주문 상품 생성 및 재고 차감 (핵심)
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // 재고 차감 (실패 시 예외 발생 -> 전체 롤백)
            product.removeStock(cartItem.getQty());

            // 주문 상세 저장
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .qty(cartItem.getQty())
                    .priceAtOrder(product.getPrice())
                    .build();
            orderItemRepository.save(orderItem);
        }

        // 4. 장바구니 비우기
        cartItemRepository.deleteAllByCart_CartId(cart.getCartId());

        return order.getOrderId();
    }
}