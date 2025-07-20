package com.pettact.api.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.payment.dto.PaymentConfirmRequestDTO;
import com.pettact.api.payment.dto.PaymentResponseDTO;
import com.pettact.api.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
	
	private final PaymentService paymentService;
	
    /**
     * ✅ 결제 승인 요청 처리 (Toss Payments SDK 완료 후 호출됨)
     * @param request paymentKey, orderId, amount 포함
     * @return 결제 결과 DTO
     */
	@PostMapping("/confirm")
	public ResponseEntity<PaymentResponseDTO>confirmPayment(@RequestBody PaymentConfirmRequestDTO request){
		

	    // ✅ 요청 데이터 로깅
	    System.out.println("📥 결제 승인 요청 수신");
	    System.out.println("paymentKey: " + request.getPaymentKey());
	    System.out.println("orderId: " + request.getOrderId());
	    System.out.println("orderNo: " + request.getOrderNo());
	    System.out.println("amount: " + request.getAmount());
	    
        PaymentResponseDTO response = paymentService.confirmPayment(request);
        System.out.println("✅ 반환할 응답 DTO: " + response); 
        return ResponseEntity.ok(response);
	}
}
