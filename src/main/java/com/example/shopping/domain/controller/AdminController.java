package com.example.shopping.domain.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopping.domain.dto.AdminDto;
import com.example.shopping.domain.dto.StatDto;
import com.example.shopping.domain.service.OrderService;
import com.example.shopping.domain.service.StatisticsService;
import com.example.shopping.domain.service.UserService;
import com.example.shopping.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminController {

    private StatisticsService statisticsService;
    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;
    private OrderService orderService;

    private Long getUserId(String token) {
        return Long.parseLong(jwtTokenProvider.getUserPk(token.substring(7)));
    }

    // 일별 매출 통계
    @GetMapping("/sales/daily")
    public ResponseEntity<List<StatDto.DailySales>> getDailySales(@RequestHeader("Authorization") String token) {
        Long userId = getUserId(token);
        return ResponseEntity.ok(statisticsService.getDailySales(userId));
    }

    // 카테고리별 판매 통계
    @GetMapping("/sales/category")
    public ResponseEntity<List<StatDto.CategorySales>> getCategorySales(@RequestHeader("Authorization") String token) {
        Long userId = getUserId(token);
        return ResponseEntity.ok(statisticsService.getCategorySales(userId));
    }

    // 회원 목록 조회
    @GetMapping("/users")
    public ResponseEntity<List<AdminDto.UserResponse>> getAllUsers(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.getAllUsers(getUserId(token)));
    }

    // 회원 상태 변경 (정지 등)
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<String> updateUserStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestBody AdminDto.UpdateUserStatus request
    ) {
        userService.updateUserStatus(getUserId(token), userId, request.getStatus());
        return ResponseEntity.ok("회원 상태가 변경되었습니다.");
    }

    // 전체 주문 조회
    @GetMapping("/orders")
    public ResponseEntity<List<AdminDto.AdminOrderResponse>> getAllOrders(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(orderService.getAllOrders(getUserId(token)));
    }
}