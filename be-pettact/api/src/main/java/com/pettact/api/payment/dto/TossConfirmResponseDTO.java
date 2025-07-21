package com.pettact.api.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TossConfirmResponseDTO { // 받아야 할 데이터
	
	 	private String paymentKey;
	 	private String orderId;
	    private Long orderNo;
	    
	    @JsonProperty("totalAmount")
	    private Long amount;
	    private String method;
	    private String status;
	    private String approvedAt;

	    private Card card; // JSON 안에 card.company → card 필드로 매핑됨

	    @Data
	    public static class Card {
	        private String company; // 카드사 이름
	        private String issuerCode;
	        private String acquirerCode;
	        private String number;
	        private int installmentPlanMonths;
	        
	        @JsonProperty("isInterestFree")
	        private boolean interestFree;
	        private String interestPayer;
	        private String approveNo;
	        private boolean useCardPoint;
	        private String cardType;
	        private String ownerType;
	        private String acquireStatus;
	        private int amount;
	        
	    }

	     //🎯 card.company 값을 꺼내는 헬퍼 메서드
	    public String getCardCompany() {
	        return card != null ? card.getCompany() : null;
	    }

}
