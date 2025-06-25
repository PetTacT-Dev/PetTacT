package com.pettact.api.product.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
	//상품 전체 DTO
    private Long id;              // 상품 번호
    private String name;          // 상품 이름
    private String description;   // 상품 설명
    private Integer price;        // 상품 가격
    private Integer quantity;     // 상품 수량
    private LocalDateTime createdAt; // 상품 등록 시간
    private boolean status;       // true: 판매중, false: 품절
    private boolean deleted;	  // 상품 삭제여부
    
    private Long categoryId;		// 상품 카테고리 ID
    private String categoryName;      // 상품 카테고리 이름
}
