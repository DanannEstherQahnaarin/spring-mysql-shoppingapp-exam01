package com.example.shopping.domain.entity.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품 카테고리 엔티티
 * 
 * <p>쇼핑몰에서 판매하는 상품들을 분류하기 위한 카테고리를 관리하는 엔티티입니다.
 * 예: "전자제품", "의류", "식품", "도서" 등
 * 
 * <p>관계:
 * <ul>
 *   <li>Product와 1:N 관계를 맺습니다. 하나의 카테고리에 여러 상품이 속할 수 있습니다.</li>
 * </ul>
 * 
 * <p>제약사항:
 * <ul>
 *   <li>카테고리 이름은 고유해야 합니다 (unique = true).</li>
 *   <li>카테고리 이름은 필수 입력 항목입니다 (nullable = false).</li>
 *   <li>카테고리 이름은 최대 30자까지 입력 가능합니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Category {
    
    /**
     * 카테고리 고유 ID (Primary Key)
     * 데이터베이스에서 자동으로 증가하는 값입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * 카테고리 이름
     * 상품을 분류하는 카테고리의 이름입니다.
     * 고유해야 하며, 필수 입력 항목입니다. 최대 30자까지 입력 가능합니다.
     * 예: "전자제품", "의류", "식품", "도서" 등
     */
    @Column(unique = true, nullable = false, length = 30)
    private String name;

    public void updateName(String name) {
        this.name = name;
    }
}
