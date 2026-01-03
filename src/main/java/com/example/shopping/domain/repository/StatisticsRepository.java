package com.example.shopping.domain.repository;

import java.time.LocalDate;
import java.util.List;

import com.example.shopping.domain.dto.StatDto;

public interface StatisticsRepository {
    // 최근 7일간 일별 매출
    List<StatDto.DailySales> findDailySales(LocalDate startDate, LocalDate endDate);

    // 카테고리별 판매 통계
    List<StatDto.CategorySales> findCategorySales();
}