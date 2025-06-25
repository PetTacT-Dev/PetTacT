package com.pettact.api.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateDTO { 
	
	//상품 수정 전용 DTO
	@NotBlank(message = "상품 이름은 필수입니다.")
    private String productsName;
	
	@NotBlank(message = "상품 설명은 필수입니다.")
    private String productsDescription;
	
    @NotNull(message = "가격을 입력하세요.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer productsPrice;
    
    @NotNull(message = "재고 수량을 입력하세요.")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private Integer productsStock;
    
    @NotNull(message = "카테고리를 입력하세요.")
    private Long categoryId; //카테고리 id
    
    private boolean productsStatus;
}
