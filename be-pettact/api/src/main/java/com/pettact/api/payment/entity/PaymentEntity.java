package com.pettact.api.payment.entity;

import com.pettact.api.order.entity.OrderEntity;
import com.pettact.api.payment.dto.TossConfirmResponseDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
	private Long id;
	
	@Column(nullable = false, unique = true ) // 필수 & 반복 금지
	private String paymentKey; // 토스에서 전달한 고유 결제 키
	
	@Column(nullable = false)
	private String orderId; // toss번호 
	
	@OneToOne
	@JoinColumn(name = "order_no")
	private OrderEntity order;; // 주문번호 (우리가 정한 주문 ID)
	

    @Column(nullable = false)
    private Long amount; // 결제 금액 (단위: 원)

    private String method; // 결제 수단 (예: 카드, 가상계좌 등)

    private String status; // 결제 상태 (예: DONE)

    private String approvedAt; // 결제 승인 시각 (ISO-8601 문자열)

    private String cardCompany; // 카드사 이름 (예: 신한, 현대 등)

	public static PaymentEntity fromTossResponse(TossConfirmResponseDTO dto) { // 토스에서 받은 결제 정보를 우리 DB에 맞는 형식으로 변환
		
		return PaymentEntity.builder()
				.paymentKey(dto.getPaymentKey())
				.orderId(dto.getOrderId())
				.amount(dto.getAmount())
		        .method(dto.getMethod())
		        .status(dto.getStatus())
		        .approvedAt(dto.getApprovedAt())
		        .cardCompany(dto.getCard().getCompany())  // 예시: 카드 정보 중 카드사 이름
		        .build();		
	}
	
//	@OneToOne(mappedBy = "payment")
//	private OrderEntity order;

	
}
