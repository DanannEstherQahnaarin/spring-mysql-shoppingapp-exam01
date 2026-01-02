package com.example.shopping.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.shopping.domain.entity.order.Orders;

public interface OrdersRepositoryC{
    List<Orders> findByUserIdWithItems(Long id);

    Optional<Orders> findOrderDetail(Long id);
}
