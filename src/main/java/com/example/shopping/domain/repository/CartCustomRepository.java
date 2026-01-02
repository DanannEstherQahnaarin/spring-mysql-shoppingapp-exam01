package com.example.shopping.domain.repository;

import java.util.List;

import com.example.shopping.domain.dto.OrderDto;

public interface CartCustomRepository {
    List<OrderDto.CartItemResponse> findCartItemsByUserId(Long userId);
}
