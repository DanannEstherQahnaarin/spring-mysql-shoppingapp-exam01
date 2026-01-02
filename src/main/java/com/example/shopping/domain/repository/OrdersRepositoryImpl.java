package com.example.shopping.domain.repository;

import static com.example.shopping.domain.entity.order.QOrders.orders;
import static com.example.shopping.domain.entity.order.QOrderItem.orderItem;
import static com.example.shopping.domain.entity.product.QProduct.product;

import java.util.List;
import java.util.Optional;

import com.example.shopping.domain.entity.order.Orders;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class OrdersRepositoryImpl implements OrdersRepositoryC {

    JPAQueryFactory queryFactory;

    @Override
    public Optional<Orders> findOrderDetail(Long orderId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(orders)
                        .distinct()
                        .leftJoin(orders.orderItems, orderItem).fetchJoin()
                        .leftJoin(orderItem.product, product).fetchJoin()
                        .where(orders.orderId.eq(orderId))
                        .fetchOne());
    }

    @Override
    public List<Orders> findByUserIdWithItems(Long userId) {
        return queryFactory
                .selectFrom(orders)
                .distinct() // 1:N 조인이므로 중복된 Orders 제거
                .leftJoin(orders.orderItems, orderItem).fetchJoin() // 주문 상품 같이 가져오기
                .leftJoin(orderItem.product, product).fetchJoin() // 상품 정보도 같이 가져오기
                .where(orders.userId.eq(userId))
                .orderBy(orders.orderId.desc())
                .fetch();
    }

}
