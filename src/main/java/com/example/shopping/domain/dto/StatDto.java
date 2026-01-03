package com.example.shopping.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StatDto {

    // 일별 매출 통계
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySales {
        private String date;        // 날짜 (YYYY-MM-DD)
        private Long totalSales;    // 총 매출액
        private Long orderCount;    // 주문 건수
    }

    // 카테고리별 판매량
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySales {
        private String categoryName;
        private Long totalQty;      // 총 판매 개수
        private Long totalSales;    // 총 판매 금액
    }
}