package com.pettact.api.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pettact.api.order.entity.OrderEntity;
import com.pettact.api.order.repository.OrderRepository;
import com.pettact.api.payment.client.TossPaymentClient;
import com.pettact.api.payment.dto.PaymentConfirmRequestDTO;
import com.pettact.api.payment.dto.PaymentResponseDTO;
import com.pettact.api.payment.dto.TossConfirmResponseDTO;
import com.pettact.api.payment.entity.PaymentEntity;
import com.pettact.api.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    
    @Transactional // ✅ 이거 추카
    public PaymentResponseDTO confirmPayment(PaymentConfirmRequestDTO request) {
    	
        System.out.println("▶️ confirmPayment 메서드 시작됨");
        System.out.println("요청 paymentKey = " + request.getPaymentKey());
        System.out.println("요청 orderId = " + request.getOrderId());
        System.out.println("요청 amount = " + request.getAmount());
        try {
            // 1. 토스에 결제 승인 요청을 보냄
            TossConfirmResponseDTO tossResponse = tossPaymentClient.confirmPayment(
            		
                    request.getPaymentKey(),
                    request.getOrderId(),
                    request.getAmount()
            );
            

            System.out.println("▶️ tossPaymentClient.confirmPayment 호출 완료");
            System.out.println("tossResponse amount = " + tossResponse.getAmount());

            // 2. 응답으로 받은 데이터로 Entity 생성
            PaymentEntity payment = PaymentEntity.fromTossResponse(tossResponse);
            
            System.out.println("🛰️ Toss API 응답 amount: " + tossResponse.getAmount());

            // 3. DB에 저장
            paymentRepository.save(payment);
            
            System.out.println("▶️ paymentRepository.save 완료, 저장된 payment id: " + payment.getId());

            // ✅ 3. 주문과 연동 - 여기 추가!
            OrderEntity order = orderRepository.findByOrderNo(request.getOrderNo())
            	    .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));
            order.setPayment(payment);
            payment.setOrder(order); // ❗이 코드가 필요할 수 있음
            orderRepository.save(order); // payment_id 저장됨
            System.out.println("▶️ orderRepository.save 완료, order에 payment 연결됨");

            // 4. DTO로 변환해서 클라이언트에 반환
            return PaymentResponseDTO.fromEntity(payment);

        } catch (Exception e) {
            System.err.println("❌ 결제 승인 처리 중 에러 발생: " + e.getMessage());
            e.printStackTrace(); // 스택 트레이스 출력

            // 필요하다면 예외 재던지기 or 커스텀 예외 반환 가능
            throw e; 
        }
    }
}
