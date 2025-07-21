package com.pettact.api.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//결제 요청용 DTO 예시
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
	
 private String orderId;
 private Long orderNo;  // 주문번호 (OrderEntity.orderNo)
 private Long amount;     // 결제 금액 (OrderEntity.totalPrice)
 private String method;   // 결제 수단 (예: CARD, VBANK 등)
}

