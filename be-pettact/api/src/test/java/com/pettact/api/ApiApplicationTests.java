package com.pettact.api;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {
	
//    @Autowired
//    private ProductRepository productRepository;
//    
//    @Autowired
//    private CartRepository cartRepository;
    
//    private static final Long TEMP_USER_ID = 1L;
//    
//    @Test
//    @DisplayName("장바구니에 20개 샘플 상품 추가")
//    void addSampleCartItems() {
//        List<ProductEntity> products = productRepository.findAll();
//
//        if (products.size() < 20) {
//            throw new IllegalStateException("샘플 상품이 최소 20개 필요합니다.");
//        }
//
//        for (int i = 0; i < 20; i++) {
//            ProductEntity product = products.get(i);
//
//            CartEntity cartProduct = CartEntity.builder()
//                    .userId(TEMP_USER_ID)
//                    .product(product)
//                    .quantity((i % 5) + 1)  // 수량: 1~5 반복
//                    .build();
//
//            cartRepository.save(cartProduct);
//        }
//
//        List<CartEntity> saved = cartRepository.findByUserId(TEMP_USER_ID);
//        assertEquals(20, saved.size());
//    }
//    @Test
//    void insertMultipleSampleProducts() {
//        IntStream.rangeClosed(1, 50).forEach(i -> {
//            ProductEntity product = ProductEntity.builder()
//                    .name("테스트 상품 " + i)
//                    .description("샘플 설명입니다. 번호: " + i)
//                    .price(10000 + i * 100)
//                    .quantity(10 + i)
//                    .createdAt(LocalDateTime.now())
//                    .category("카테고리" + (i % 5 + 1))  // 예: 카테고리1 ~ 카테고리5
//                    .status(i % 2 == 0) // 짝수: 판매중, 홀수: 품절
//                    .build();
//
//            productRepository.save(product);
//        });
//
//        System.out.println("샘플 상품 50개 저장 완료!");
//    }
//	@Test
//	void contextLoads() {
//	}

}
