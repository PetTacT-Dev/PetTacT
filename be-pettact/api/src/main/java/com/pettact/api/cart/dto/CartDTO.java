package com.pettact.api.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
	
    private Long productId;       // 상품 ID
    private String productName;   // 상품 이름
    private Integer productPrice; // 상품 가격
    private Integer quantity;     // 수량
    private Integer totalPrice;   // 총 가격 (상품 가격 * 수량)
    
}
