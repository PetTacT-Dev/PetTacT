package com.pettact.api.product.dto;

import java.util.List;

import com.pettact.api.multiFile.entity.MultiFile;

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
	private String productName;
	
	@NotBlank(message = "상품 내용은 필수입니다.")
	private String productDescription;
	
    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
	private Integer productPrice;
    
    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
	private Integer productStock;
    
    @NotNull(message = "카테고리를 선택하세요.")
    private Long categoryNo;
	private boolean productStatus;
	
//	private String imageUrl;
//	
//	@NotNull(message = "이미지 1장은 필수입니다.")
//	private MultiFile multiFile;
	
	private List<String> imageUrls;

}
