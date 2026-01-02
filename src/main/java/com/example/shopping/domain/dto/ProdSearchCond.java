package com.example.shopping.domain.dto;

import lombok.Data;

@Data
public class ProdSearchCond {
    private String keyword;       // 상품명 검색
    private Long categoryId;      // 카테고리 필터
    private Integer minPrice;     // 최소 가격
    private Integer maxPrice;     // 최대 가격
    private Boolean inStock;      // 재고 있음 여부 (true면 재고 > 0 인 것만)
}
