package com.pettact.api.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartAddDTO {
	// 장바구니 상품 등록 DTO
	@NotNull
    private Long productId;
	@Min(1)
    private int quantity;
}
