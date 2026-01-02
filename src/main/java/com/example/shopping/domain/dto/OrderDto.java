package com.example.shopping.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 주문 관련 DTO 클래스
 * 
 * <p>주문, 장바구니 관련 요청 및 응답 데이터를 전송하기 위한 DTO 클래스들을
 * 포함하는 외부 클래스입니다. 내부 정적 클래스로 요청/응답 DTO를 정의합니다.
 * 
 * <p>사용 목적:
 * <ul>
 *   <li>Controller와 Service 계층 간 데이터 전송</li>
 *   <li>API 요청/응답 스펙 정의</li>
 *   <li>장바구니 및 주문 정보 표현</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
public class OrderDto {

    /**
     * 장바구니 담기 요청 DTO
     * 
     * <p>상품을 장바구니에 추가할 때 사용하는 요청 객체입니다.
     * 
     * <p>포함 정보:
     * <ul>
     *   <li>productId: 장바구니에 담을 상품의 ID</li>
     *   <li>qty: 담을 수량 (양수 값)</li>
     * </ul>
     * 
     * <p>비즈니스 로직:
     * <ul>
     *   <li>같은 상품이 이미 장바구니에 있으면 수량을 증가시킵니다.</li>
     *   <li>같은 상품이 없으면 새로운 장바구니 항목을 생성합니다.</li>
     * </ul>
     * 
     * <p>사용 예:
     * <pre>
     * OrderDto.AddToCart request = new OrderDto.AddToCart();
     * request.setProductId(1L);
     * request.setQty(3);
     * </pre>
     */
    @Data
    public static class AddToCart {
        /** 상품 ID - 장바구니에 담을 상품의 고유 ID */
        private Long productId;
        
        /** 수량 - 담을 상품의 수량 (양수 값) */
        private Integer qty;
    }

    /**
     * 장바구니 항목 응답 DTO
     * 
     * <p>장바구니 조회 시 반환되는 응답 객체입니다.
     * 장바구니에 담긴 각 상품 항목의 정보를 담습니다.
     * 
     * <p>포함 정보:
     * <ul>
     *   <li>cartItemId: 장바구니 항목 ID</li>
     *   <li>productId: 상품 ID</li>
     *   <li>productName: 상품명</li>
     *   <li>price: 현재 상품 가격</li>
     *   <li>qty: 장바구니에 담은 수량</li>
     * </ul>
     * 
     * <p>주의사항:
     * <ul>
     *   <li>price는 현재 상품 가격을 나타냅니다. 주문 시점의 가격은 OrderItem에서 관리됩니다.</li>
     * </ul>
     * 
     * <p>사용 예:
     * <pre>
     * List&lt;OrderDto.CartItemResponse&gt; cartItems = orderService.getCartItems(userId);
     * </pre>
     */
    @Data
    public static class CartItemResponse {
        /** 장바구니 항목 고유 ID */
        private Long cartItemId;
        
        /** 상품 ID */
        private Long productId;
        
        /** 상품명 */
        private String productName;
        
        /** 현재 상품 가격 (단위: 원) */
        private Integer price;
        
        /** 장바구니에 담은 수량 */
        private Integer qty;
    }

    @Data
    public static class OrderResponse {
        private Long orderId;
        private String status;
        private LocalDateTime orderedAt;
        private String representativeProductName; // "상품A 외 2건"
        private Integer totalAmount;
    }

    // 주문 상세 조회용
    @Data
    public static class OrderDetailResponse {
        private Long orderId;
        private String status;
        private LocalDateTime orderedAt;
        private List<CartItemResponse> orderItems; // 기존 CartItemResponse 재사용 (구조가 비슷하므로)
        private Integer totalAmount;
    }
}