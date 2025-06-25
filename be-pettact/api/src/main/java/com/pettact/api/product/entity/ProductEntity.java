package com.pettact.api.product.entity;

import com.pettact.api.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity extends BaseEntity {
	
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long productsNo; // 상품 번호

    @Column(nullable = false, length = 100)
    private String productsName; // 상품 이름

    @Column(columnDefinition = "TEXT")
    private String productsDescription; // 상품 설명

    @Column(nullable = false)
    private Integer productsPrice; // 상품 가격

    @Column(nullable = false)
    private Integer productsStock; // 상품 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "products_category") // FK 컬럼명 그대로 사용
    private ProductCategoryEntity productsCategory;// 상품 카테고리

    @Column(nullable = false)
    private boolean productsStatus; //  (1)true: 판매중, (0)false: 판매완료

//    @Column(nullable = false)
//    private boolean productsDeleted; // 삭제 여부 ((1)true: 삭제완료 , (0)false: 미삭제 )

    
}

