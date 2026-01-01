package com.example.shopping.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 상품 관련 DTO 클래스
 * 
 * <p>상품과 카테고리 관련 요청 및 응답 데이터를 전송하기 위한 DTO 클래스들을
 * 포함하는 외부 클래스입니다. 내부 정적 클래스로 요청/응답 DTO를 정의합니다.
 * 
 * <p>사용 목적:
 * <ul>
 *   <li>Controller와 Service 계층 간 데이터 전송</li>
 *   <li>API 요청/응답 스펙 정의</li>
 *   <li>데이터 검증 (Jakarta Validation)</li>
 *   <li>QueryDSL Projections를 통한 조회 결과 매핑</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
public class ProductDto {
    
    /**
     * 카테고리 생성 요청 DTO
     * 
     * <p>새로운 상품 카테고리를 생성할 때 사용하는 요청 객체입니다.
     * 
     * <p>검증 규칙:
     * <ul>
     *   <li>name: 필수 입력 (NotBlank), 고유해야 함</li>
     * </ul>
     * 
     * <p>사용 예:
     * <pre>
     * ProductDto.CreateCategory request = new ProductDto.CreateCategory();
     * request.setName("전자제품");
     * </pre>
     */
    @Data
    public static class CreateCategory {
        /** 카테고리 이름 - 상품을 분류하는 카테고리의 이름 (예: "전자제품", "의류") */
        @NotBlank
        private String name;
    }

    /**
     * 상품 생성 요청 DTO
     * 
     * <p>새로운 상품을 등록할 때 사용하는 요청 객체입니다.
     * 
     * <p>검증 규칙:
     * <ul>
     *   <li>categoryId: 필수 입력 (NotNull), 존재하는 카테고리 ID여야 함</li>
     *   <li>name: 필수 입력 (NotBlank)</li>
     *   <li>price: 필수 입력 (NotNull), 0 이상의 값</li>
     *   <li>stock: 필수 입력 (NotNull), 0 이상의 값</li>
     * </ul>
     * 
     * <p>사용 예:
     * <pre>
     * ProductDto.CreateProduct request = new ProductDto.CreateProduct();
     * request.setCategoryId(1L);
     * request.setName("노트북");
     * request.setPrice(1500000);
     * request.setStock(10);
     * </pre>
     */
    @Data
    public static class CreateProduct {
        /** 카테고리 ID - 상품이 속할 카테고리의 ID */
        @NotNull
        private Long categoryId;

        /** 상품명 - 상품의 이름 */
        @NotBlank
        private String name;

        /** 상품 가격 - 상품의 판매 가격 (단위: 원) */
        @NotNull 
        @Min(0)
        private Integer price;

        /** 재고 수량 - 상품의 재고 수량 */
        @NotNull 
        @Min(0)
        private Integer stock;
    }

    /**
     * 상품 조회 응답 DTO
     * 
     * <p>상품 목록 조회 시 반환되는 응답 객체입니다.
     * QueryDSL의 Projections.fields()를 사용하여 엔티티에서 직접 매핑됩니다.
     * 
     * <p>포함 정보:
     * <ul>
     *   <li>productId: 상품 ID</li>
     *   <li>categoryName: 카테고리 이름 (조인을 통해 가져옴)</li>
     *   <li>name: 상품명</li>
     *   <li>price: 상품 가격</li>
     *   <li>stock: 재고 수량</li>
     * </ul>
     * 
     * <p>사용 예:
     * <pre>
     * List&lt;ProductDto.Response&gt; products = productRepository.findAllProducts();
     * </pre>
     */
    @Data
    public static class Response {
        /** 상품 고유 ID */
        private Long productId;
        
        /** 카테고리 이름 - 상품이 속한 카테고리의 이름 */
        private String categoryName;
        
        /** 상품명 */
        private String name;
        
        /** 상품 가격 (단위: 원) */
        private Integer price;
        
        /** 재고 수량 */
        private Integer stock;
        
        /** QueryDSL Projections.constructor()를 위한 생성자 */
        public Response(Long productId, String categoryName, String name, Integer price, Integer stock) {
            this.productId = productId;
            this.categoryName = categoryName;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }
    }
}
