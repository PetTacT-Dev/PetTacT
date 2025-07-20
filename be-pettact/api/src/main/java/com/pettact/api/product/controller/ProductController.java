package com.pettact.api.product.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.product.dto.ProductCategoryDTO;
import com.pettact.api.product.dto.ProductCreateDTO;
import com.pettact.api.product.dto.ProductDTO;
import com.pettact.api.product.dto.ProductDetailDTO;
import com.pettact.api.product.dto.ProductUpdateDTO;
import com.pettact.api.product.service.ProductService;
import com.pettact.api.security.vo.CustomUserDetails;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private final ProductService productService;
	//단일 상품 조회, 페이징 처리
	
	
    @GetMapping("/categories")
    public ResponseEntity<?> getCategoryList() {
        List<ProductCategoryDTO> categoryList = productService.getCategoryList();
        return ResponseEntity.ok(Map.of("items", categoryList));
    }
	
	// 상품 상세보기 
	@GetMapping("/{productNo}")
	public ResponseEntity <ProductDetailDTO> getProductDetail(@PathVariable("productNo") Long productNo, HttpSession session) {
		String sessionId = session.getId();
	    ProductDetailDTO myProduct = productService.getProductDetail(productNo, sessionId);
	    return ResponseEntity.ok(myProduct);
	}

	// 상품 목록
	@GetMapping
	public ResponseEntity<Page<ProductDTO>> getAllProduct(
	        @RequestParam(name = "keyword", required = false) String keyword,
	        @RequestParam(name = "categoryNo", required = false) Long categoryNo,
	        @RequestParam(name = "sort", required = false) String sort,
	        @RequestParam(name = "page", defaultValue = "0") int page,
	        @RequestParam(name = "size", defaultValue = "12") int size) {

	    Page<ProductDTO> products = productService.getAllProduct(keyword, categoryNo, sort, page, size);
	    return ResponseEntity.ok(products);
	}
	
    // 상품 삭제 (논리 삭제)
    @PatchMapping("/delete/{productNo}")
    public ResponseEntity<String> deleteProduct(@PathVariable("productNo") Long productNo, @AuthenticationPrincipal CustomUserDetails user) throws Exception{
    	try {
            productService.deleteProduct(productNo, user.getUser());
            return ResponseEntity.ok("상품이 삭제되었습니다.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("상품을 찾을 수 없습니다.");
        }
    }
    
    // 상품 수정 
    @PutMapping(value = "/update/{productNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProduct(@PathVariable("productNo") Long productNo, @RequestPart("dto") @Valid ProductUpdateDTO dto, @RequestPart(value = "files", required = false) List<MultipartFile> files ,@AuthenticationPrincipal CustomUserDetails user){
    	productService.updateProduct(productNo, dto,files,user);
    	return ResponseEntity.ok("상품이 수정되었습니다.");
    }
    
    // 상품 등록
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createProduct(
            @RequestPart("product") String productJson,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails user) {
        
        System.out.println("컨트롤러 진입 성공!");
        System.out.println("productJson: " + productJson);
        
        if (files == null) {
            System.out.println("files is null");
        } else if (files.isEmpty()) {
            System.out.println("files is empty");
        } else {
            System.out.println("files 개수: " + files.size());
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                System.out.println("file[" + i + "] - originalName: " + file.getOriginalFilename() 
                    + ", size: " + file.getSize() 
                    + ", contentType: " + file.getContentType());
            }
        }
        ProductCreateDTO dto;
        try {
            dto = objectMapper.readValue(productJson, ProductCreateDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "잘못된 product JSON 데이터입니다."));
        }
        Long productNo = productService.createProduct(dto, files, user);
        return ResponseEntity.ok(Map.of("productNo", productNo));
    }
    
    // ------------------ 인기 상품 TOP 10 ------------------
    @GetMapping("/popular")
    public List<ProductDTO> getPopularProducts(
    		@RequestParam(value = "categoryNo", required = false) Long categoryNo,
    		@RequestParam(value = "count" , defaultValue = "5") int count) {
        return productService.getPopularProducts(categoryNo, count);
    }
}
