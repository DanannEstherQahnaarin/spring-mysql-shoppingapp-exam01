package com.example.shopping.domain.dto;

import java.time.LocalDateTime;

import lombok.Data;

public class AdminDto {

    // 회원 목록 조회 응답
    @Data
    public static class UserResponse {
        private Long userId;
        private String loginId;
        private String name;
        private String email;
        private String status; // ACTIVE, SUSPENDED 등
        private LocalDateTime joinedAt;
    }

    // 회원 상태 변경 요청
    @Data
    public static class UpdateUserStatus {
        private String status; // ACTIVE, SUSPENDED
    }
    
    // 전체 주문 목록 조회 응답 (기존 OrderDto.OrderResponse 확장)
    @Data
    public static class AdminOrderResponse {
        private Long orderId;
        private Long userId;        // 주문자 ID
        private String userName;    // 주문자명
        private String status;
        private LocalDateTime orderedAt;
        private String productName;
        private Integer totalAmount;
    }
}