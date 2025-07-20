package com.pettact.api.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.order.dto.OrderDTO;
import com.pettact.api.order.dto.OrderResponseDTO;
import com.pettact.api.order.service.OrderService;
import com.pettact.api.payment.dto.PaymentRequestDTO;
import com.pettact.api.security.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    
    // 결제 요청 생성 API 예시
//    @PostMapping("/{orderNo}/payment")
//    public ResponseEntity<PaymentRequestDTO> requestPayment(
//            @PathVariable Long orderNo,
//            @RequestParam String method // 결제 수단 (ex: 카드, 페이팔 등)
//    ) {
//        // 1. OrderService의 결제 요청 DTO 생성 메서드 호출
//        PaymentRequestDTO paymentRequest = orderService.createPaymentRequest(orderNo, method);
//
//        // 2. 여기서 실제 결제 API 호출이나 결제 화면 연결 로직 추가 가능
//        // (현재는 DTO만 반환)
//
//        return ResponseEntity.ok(paymentRequest);
//    }
    
    // 주문 등록 
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@AuthenticationPrincipal CustomUserDetails user,@RequestBody OrderDTO.CreateRequest request) {
        return ResponseEntity.ok(orderService.createOrder(user.getUserEntity(), request));
    }
    
    // 주문 내역 목록 
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getUserOrders(@AuthenticationPrincipal CustomUserDetails user,
    		@RequestParam(name = "page", defaultValue = "0" )int page,
    		@RequestParam(name = "size", defaultValue = "10") int size) {
    	
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderDTO> ordersPage = orderService.getOrdersByUser(user.getUserEntity(), pageable);
        return ResponseEntity.ok(ordersPage);
        //return ResponseEntity.ok(orderService.getOrdersByUser(user.getUserEntity()));
    }
    
    // 주문 상세
    @GetMapping("/detail/{orderNo}")
    public ResponseEntity<OrderResponseDTO> getOrderDetail(@PathVariable("orderNo") Long orderNo, @AuthenticationPrincipal CustomUserDetails user){
    	
    	OrderResponseDTO dto = orderService.getOrderDetail(orderNo, user.getUserEntity());
    	return ResponseEntity.ok(dto);
    }
    
    // 주문 취소
    @PatchMapping("/cancel/{orderNo}")
    public ResponseEntity<String> cancelOrder(@PathVariable("orderNo") Long orderNo,
                                            @AuthenticationPrincipal CustomUserDetails user) {
        String message = orderService.cancelOrder(orderNo, user.getUserEntity());
        return ResponseEntity.ok(message);
    }
}