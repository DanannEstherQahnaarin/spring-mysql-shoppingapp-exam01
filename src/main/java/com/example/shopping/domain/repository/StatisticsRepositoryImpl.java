package com.example.shopping.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.shopping.domain.dto.StatDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import static com.example.shopping.domain.entity.order.QOrders.orders;
import static com.example.shopping.domain.entity.order.QOrderItem.orderItem;
import static com.example.shopping.domain.entity.product.QProduct.product;
import static com.example.shopping.domain.entity.product.QCategory.category;

@Repository
@RequiredArgsConstructor
public class StatisticsRepositoryImpl implements StatisticsRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StatDto.DailySales> findDailySales(LocalDate startDate, LocalDate endDate) {

        // MySQL Date Format 함수 호출: DATE_FORMAT(ordered_at, '%Y-%m-%d')
        StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                orders.orderedAt,
                "%Y-%m-%d");

        return queryFactory
                .select(Projections.fields(StatDto.DailySales.class,
                        formattedDate.as("date"),
                        orderItem.priceAtOrder.multiply(orderItem.qty).sum().longValue().as("totalSales"),
                        orders.countDistinct().as("orderCount")))
                .from(orderItem)
                .join(orderItem.order, orders)
                .where(
                        orders.orderedAt.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)),
                        orders.status.ne("cancel") // 취소된 주문 제외
                )
                .groupBy(formattedDate)
                .orderBy(formattedDate.asc())
                .fetch();
    }

    @Override
    public List<StatDto.CategorySales> findCategorySales() {
        return queryFactory
                .select(Projections.fields(StatDto.CategorySales.class,
                        category.name.as("categoryName"),
                        orderItem.qty.sum().longValue().as("totalQty"),
                        orderItem.priceAtOrder.multiply(orderItem.qty).sum().longValue().as("totalSales")))
                .from(orderItem)
                .join(orderItem.product, product)
                .join(product.category, category)
                .join(orderItem.order, orders)
                .where(orders.status.ne("cancel")) // 취소된 주문 제외
                .groupBy(category.name)
                .orderBy(orderItem.qty.sum().desc()) // 많이 팔린 순
                .fetch();
    }

}
