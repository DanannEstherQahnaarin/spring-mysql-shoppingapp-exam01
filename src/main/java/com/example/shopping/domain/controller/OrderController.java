package com.example.shopping.domain.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopping.domain.dto.OrderDto;
import com.example.shopping.domain.service.OrderService;
import com.example.shopping.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

/**
 * 주문 컨트롤러
 * 
 * <p>
 * 주문과 장바구니 관련 REST API 엔드포인트를 제공하는 컨트롤러입니다.
 * 장바구니 담기, 장바구니 조회, 주문하기 등의 기능을 처리합니다.
 * 
 * <p>
 * API 경로:
 * <ul>
 * <li>기본 경로: /api/orders</li>
 * </ul>
 * 
 * <p>
 * 제공 엔드포인트:
 * <ul>
 * <li>POST /api/orders/cart: 장바구니 담기 (인증 필요)</li>
 * <li>GET /api/orders/cart: 장바구니 조회 (인증 필요)</li>
 * <li>POST /api/orders: 주문하기 (인증 필요)</li>
 * </ul>
 * 
 * <p>
 * 인증:
 * <ul>
 * <li>모든 엔드포인트는 JWT 토큰 인증이 필요합니다.</li>
 * <li>Authorization 헤더에 "Bearer {token}" 형식으로 토큰을 전송해야 합니다.</li>
 * <li>토큰에서 사용자 ID를 추출하여 각 요청을 처리합니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    /** 주문 서비스 */
    private final OrderService orderService;

    /** JWT 토큰 제공자 */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Authorization 헤더의 JWT 토큰에서 사용자 ID를 추출합니다.
     * 
     * <p>
     * 토큰 형식:
     * <ul>
     * <li>헤더 값: "Bearer {token}"</li>
     * <li>"Bearer " 접두사를 제거한 후 토큰을 파싱합니다.</li>
     * </ul>
     * 
     * <p>
     * 주의사항:
     * <ul>
     * <li>토큰이 유효하지 않거나 형식이 올바르지 않으면 예외가 발생할 수 있습니다.</li>
     * <li>향후 토큰 검증 및 예외 처리를 추가하는 것이 권장됩니다.</li>
     * </ul>
     * 
     * @param token Authorization 헤더 값 ("Bearer {token}" 형식)
     * @return 사용자 ID
     */
    private Long getUserId(String token) {
        String tokenValue = token.substring(7); // "Bearer " 제거
        return Long.parseLong(jwtTokenProvider.getUserPk(tokenValue));
    }

    /**
     * 장바구니 담기 API
     * 
     * <p>
     * 상품을 장바구니에 추가합니다.
     * 같은 상품이 이미 있으면 수량을 증가시킵니다.
     * 
     * <p>
     * 요청:
     * <ul>
     * <li>Headers: Authorization: Bearer {token} (필수)</li>
     * <li>Content-Type: application/json</li>
     * <li>Body: OrderDto.AddToCart (상품 ID, 수량)</li>
     * </ul>
     * 
     * <p>
     * 응답:
     * <ul>
     * <li>Status: 200 OK</li>
     * <li>Body: "장바구니에 담겼습니다." (문자열)</li>
     * </ul>
     * 
     * <p>
     * 비즈니스 로직:
     * <ul>
     * <li>같은 상품이 이미 장바구니에 있으면 새 항목을 생성하지 않고 수량을 증가시킵니다.</li>
     * <li>같은 상품이 없으면 새로운 장바구니 항목을 생성합니다.</li>
     * </ul>
     * 
     * @param token   JWT 토큰 (Authorization 헤더)
     * @param request 장바구니 담기 요청 DTO
     * @return 성공 메시지를 포함한 ResponseEntity
     */
    @PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(@RequestHeader("Authorization") String token,
            @RequestBody OrderDto.AddToCart request) {
        Long userId = getUserId(token);
        orderService.addToCart(userId, request);
        return ResponseEntity.ok("장바구니에 담겼습니다.");
    }

    @PutMapping("/cart/update/{cartItemId}")
    public ResponseEntity<String> updateCartItemQty(@RequestHeader("Authorization") String token,
            @PathVariable Long cartItemId, @RequestBody OrderDto.AddToCart request // qty 필드 재사용
    ) {
        Long userId = getUserId(token);
        orderService.updateCartItemQty(userId, cartItemId, request.getQty());
        return ResponseEntity.ok("수량이 변경되었습니다.");
    }

    // 장바구니 항목 삭제
    @DeleteMapping("/cart/delete/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@RequestHeader("Authorization") String token,
            @PathVariable Long cartItemId) {
        Long userId = getUserId(token);
        orderService.deleteCartItem(userId, cartItemId);
        return ResponseEntity.ok("삭제되었습니다.");
    }

    /**
     * 장바구니 조회 API
     * 
     * <p>
     * 현재 사용자의 장바구니에 담긴 상품 목록을 조회합니다.
     * 
     * <p>
     * 요청:
     * <ul>
     * <li>Method: GET</li>
     * <li>Headers: Authorization: Bearer {token} (필수)</li>
     * </ul>
     * 
     * <p>
     * 응답:
     * <ul>
     * <li>Status: 200 OK</li>
     * <li>Body: OrderDto.CartItemResponse 리스트 (장바구니 항목 ID, 상품 ID, 상품명, 가격, 수량)</li>
     * </ul>
     * 
     * <p>
     * 특이사항:
     * <ul>
     * <li>장바구니가 없거나 비어있으면 빈 리스트를 반환합니다.</li>
     * </ul>
     * 
     * @param token JWT 토큰 (Authorization 헤더)
     * @return 장바구니 항목 목록을 포함한 ResponseEntity
     */
    @GetMapping("/cart")
    public ResponseEntity<List<OrderDto.CartItemResponse>> getCart(@RequestHeader("Authorization") String token) {
        Long userId = getUserId(token);
        return ResponseEntity.ok(orderService.getCartItems(userId));
    }

    /**
     * 주문하기 API
     * 
     * <p>
     * 장바구니의 모든 상품을 주문 처리합니다.
     * 주문 생성, 재고 차감, 주문 항목 저장, 장바구니 비우기가 하나의 트랜잭션으로 처리됩니다.
     * 
     * <p>
     * 요청:
     * <ul>
     * <li>Method: POST</li>
     * <li>Headers: Authorization: Bearer {token} (필수)</li>
     * <li>Body: 없음 (장바구니의 모든 상품을 주문)</li>
     * </ul>
     * 
     * <p>
     * 응답:
     * <ul>
     * <li>Status: 200 OK</li>
     * <li>Body: 생성된 주문 ID (Long)</li>
     * </ul>
     * 
     * <p>
     * 처리 과정:
     * <ol>
     * <li>장바구니 조회</li>
     * <li>주문 생성</li>
     * <li>각 상품의 재고 차감 (재고 부족 시 예외 발생, 전체 롤백)</li>
     * <li>주문 항목 저장 (주문 시점의 가격 저장)</li>
     * <li>장바구니 비우기</li>
     * </ol>
     * 
     * <p>
     * 데이터 무결성:
     * <ul>
     * <li>모든 작업이 하나의 트랜잭션으로 처리되어 원자성이 보장됩니다.</li>
     * <li>재고 부족 시 전체 주문이 롤백됩니다.</li>
     * </ul>
     * 
     * @param token JWT 토큰 (Authorization 헤더)
     * @return 생성된 주문 ID를 포함한 ResponseEntity
     * @throws RuntimeException 장바구니가 비어있거나, 주문할 상품이 없거나, 재고가 부족한 경우
     */
    @PostMapping("/create")
    public ResponseEntity<Long> createOrder(@RequestHeader("Authorization") String token) {
        Long userId = getUserId(token);
        Long orderId = orderService.createOrder(userId);
        return ResponseEntity.ok(orderId);
    }

    // 주문 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrderList(@RequestHeader("Authorization") String token) {
        Long userId = getUserId(token);
        return ResponseEntity.ok(orderService.getOrderList(userId));
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}/detail")
    public ResponseEntity<OrderDto.OrderDetailResponse> getOrderDetail(@RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        Long userId = getUserId(token);
        return ResponseEntity.ok(orderService.getOrderDetail(userId, orderId));
    }

    // 주문 취소
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        Long userId = getUserId(token);
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok("주문이 취소되었습니다.");
    }

    // 장바구니 전체 비우기
    @DeleteMapping("/cart/clear")
    public ResponseEntity<String> clearCart(@RequestHeader("Authorization") String token) {
        Long userId = getUserId(token);
        orderService.clearCart(userId);
        return ResponseEntity.ok("장바구니를 비웠습니다.");
    }

    // 주문 상태 및 배송 정보 변경 (관리자)
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(@RequestHeader("Authorization") String token,
            @PathVariable Long orderId, @RequestBody OrderDto.UpdateStatus request) {
        Long userId = getUserId(token);
        // Service 메서드 시그니처 변경에 맞춰 호출 (dto 전체 전달)
        orderService.updateOrderStatus(userId, orderId, request);
        return ResponseEntity.ok("주문 상태가 변경되었습니다.");
    }
}