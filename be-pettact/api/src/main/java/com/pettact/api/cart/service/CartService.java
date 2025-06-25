package com.pettact.api.cart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pettact.api.cart.dto.CartDTO;
import com.pettact.api.cart.entity.CartEntity;
import com.pettact.api.cart.repository.CartRepository;
import com.pettact.api.product.entity.ProductEntity;
import com.pettact.api.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
	
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    
    private static final Long TEMP_USER_ID = 1L; // 임시 유저
    
    
    // 장바구니 상품 목록
    public List<CartDTO> getCartProducts() {
        return cartRepository.findByUserId(TEMP_USER_ID)
                .stream()
                .map(cartProduct -> {
                    return CartDTO.builder()
                            .productId(cartProduct.getProduct().getProductsNo())
                            .productName(cartProduct.getProduct().getProductsName())
                            .productPrice(cartProduct.getProduct().getProductsPrice())
                            .quantity(cartProduct.getQuantity())
                            .totalPrice(cartProduct.getProduct().getProductsPrice() * cartProduct.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    // 장바구니 상품 삭제
    public void deleteProduct(Long productId) {
        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        CartEntity cartProduct = cartRepository.findByUserIdAndProduct(TEMP_USER_ID, product)
            .orElseThrow(() -> new IllegalArgumentException("장바구니에 상품이 없습니다."));

        cartRepository.delete(cartProduct);
    }
    
    // 장바구니 수량 업데이트
    public void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");

        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        CartEntity cartProduct = cartRepository.findByUserIdAndProduct(TEMP_USER_ID, product)
            .orElseThrow(() -> new IllegalArgumentException("장바구니에 상품이 없습니다."));

        cartProduct.setQuantity(quantity);
        cartRepository.save(cartProduct);
    }
    
    // 장바구니 상품 추가 ( 동일 상품 등록 시에 수량 증가 )
    public void addToCart(Long productId, int quantity) {
        // 수량 검증
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        CartEntity cartProduct = cartRepository.findByUserIdAndProduct(TEMP_USER_ID, product)
                .orElse(CartEntity.builder()
                        .userId(TEMP_USER_ID)
                        .product(product)
                        .quantity(0)
                        .build());

        cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
        cartRepository.save(cartProduct);
    }
}
