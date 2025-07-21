package com.pettact.api.order.dto;

import java.util.ArrayList;
import java.util.List;

import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.order.enums.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDTO extends BaseEntity{

	private Long orderNo;
    private Long userNo;
    private int totalPrice;
    private OrderStatus status;
    private List<OrderDetailDTO> orderDetailList = new ArrayList<>();
    
    private String deliveryName;   // 배송지명 (예: 우리집, 회사 등)
    private String receiver;       // 수령인 이름
    private String zipcode;        // 우편번호
    private String address1;       // 기본 주소
    private String address2;       // 상세 주소
    private String phone;          // 연락처
    
    // ✅ 2단계: 프론트에서 오는 주문 요청 DTO
    @Data
    @NoArgsConstructor
    public static class CreateRequest {
    	private String orderId;         // ✅ 추가
        private String deliveryName;   // 배송지명 (예: 우리집, 회사 등)
        private String receiver;       // 수령인 이름
        private String zipcode;        // 우편번호
        private String address1;       // 기본 주소
        private String address2;       // 상세 주소
        private String phone;          // 연락처

        private List<OrderDetailDTO> orderDetails; // 주문 상품 리스트
    }
}
