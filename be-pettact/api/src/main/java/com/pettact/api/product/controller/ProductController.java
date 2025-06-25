package com.pettact.api.product.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.product.dto.ProductCreateDTO;
import com.pettact.api.product.dto.ProductDTO;
import com.pettact.api.product.dto.ProductUpdateDTO;
import com.pettact.api.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
public class ProductController {
	
	private final ProductService productService;
	//단일 상품 조회, 페이징 처리
	 //상품 목록
	@GetMapping("/list")
	public ResponseEntity<List<ProductDTO>> getAllProducts(){
		List<ProductDTO> products = productService.getAllProducts();
		return ResponseEntity.ok(products);
		
	}
	
    //상품 삭제 (논리 삭제)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("상품이 삭제되었습니다.");
    }
    
    //상품 수정 
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable("id") Long id, @RequestBody @Valid ProductUpdateDTO dto){
    	productService.updateProduct(id, dto);
    	return ResponseEntity.ok("상품이 수정되었습니다.");
    }
    
    //상품 등록
    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody @Valid ProductCreateDTO dto) {
        productService.createProduct(dto);
        return ResponseEntity.ok("상품이 등록되었습니다.");
    }

    
}
