package com.pettact.api.payment.dto;

import lombok.Data;

/**
 * ✅ 클라이언트에서 Toss 결제 완료 후 우리 서버에 전달하는 요청 데이터
 */
@Data
public class PaymentConfirmRequestDTO {
    private String paymentKey; // 토스에서 받은 고유 결제키
    private String orderId;
    private Long orderNo;    // 우리 시스템의 주문 번호
    private Long amount;       // 결제된 금액 (단위: 원)
}
