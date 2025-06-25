package com.pettact.api.cart.controller;

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

import com.pettact.api.cart.dto.CartAddDTO;
import com.pettact.api.cart.dto.CartDTO;
import com.pettact.api.cart.dto.CartUpdateDTO;
import com.pettact.api.cart.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CartController {
	
    private final CartService cartService;
    
    // 장바구니 상품 목록 ( userid 추가 업데이트 필요  )
    @GetMapping("/list")
    public ResponseEntity<List<CartDTO>> getCartProducts() {
        return ResponseEntity.ok(cartService.getCartProducts());
    }
    
    // 장바구니 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable("productId") Long productId) {
        cartService.deleteProduct(productId);
        return ResponseEntity.ok("장바구니에서 상품이 삭제되었습니다.");
    }
    
    // 장바구니 수량 변경
    @PutMapping("/{productId}")
    public ResponseEntity<String> updateQuantity(@RequestBody @Valid CartUpdateDTO dto) {
        cartService.updateQuantity(dto.getProductId(), dto.getQuantity());
        return ResponseEntity.ok("장바구니 수량이 변경되었습니다.");
    }
    
    //장바구니 상품 추가 ( 동일 상품 등록 시에 수량 증가 )
    @PostMapping
    public ResponseEntity<String> addToCart(@RequestBody CartAddDTO dto) {
        cartService.addToCart(dto.getProductId(), dto.getQuantity());
        return ResponseEntity.ok("장바구니에 추가되었습니다.");
    }

}
