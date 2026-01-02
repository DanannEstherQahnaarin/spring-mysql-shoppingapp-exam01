package com.example.shopping.domain.repository;

import static com.example.shopping.domain.entity.order.QCart.cart;
import static com.example.shopping.domain.entity.order.QCartItem.cartItem;
import static com.example.shopping.domain.entity.product.QProduct.product;

import java.util.List;

import com.example.shopping.domain.dto.OrderDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class CartRepositoryImpl implements CartCustomRepository {

    JPAQueryFactory queryFactory;

    @Override
    public List<OrderDto.CartItemResponse> findCartItemsByUserId(Long userId) {
        return queryFactory
                .select(Projections.fields(OrderDto.CartItemResponse.class,
                        cartItem.cartItemId,
                        product.productId,
                        product.name.as("productName"),
                        product.price,
                        cartItem.qty))
                .from(cartItem)
                .join(cartItem.cart, cart)
                .join(cartItem.product, product)
                .where(cart.userId.eq(userId))
                .orderBy(cartItem.cartItemId.desc())
                .fetch();
    }

}
