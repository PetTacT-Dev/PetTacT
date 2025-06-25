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
public class ProductCreateDTO {
	
	//상품 등록 전용 DTO
	@NotBlank(message = "상품 이름은 필수입니다.")
	private String productsName;
	
	@NotBlank(message = "상품 내용은 필수입니다.")
	private String productsDescription;
	
    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
	private Integer productsPrice;
    
    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
	private Integer productsStock;
    
    @NotNull(message = "카테고리를 선택하세요.")
    private Long categoryId;
	private boolean productsStatus;
}
