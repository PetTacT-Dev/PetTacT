package com.pettact.api.order.entity;

import java.util.ArrayList;
import java.util.List;

import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.core.base.MapperUtil;
import com.pettact.api.order.dto.OrderDTO;
import com.pettact.api.order.enums.OrderStatus;
import com.pettact.api.payment.entity.PaymentEntity;
import com.pettact.api.user.entity.Users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "orders") // 주문
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends BaseEntity {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderNo;
    
    @Column(name = "order_id", unique = true)
    private String orderId; //결제 아이디
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private Users user;

    private int totalPrice;
    
    private String deliveryName; // 배송지명 (예: 우리집, 회사)
    private String receiver;
    private String zipcode;
    private String address1;
    private String address2;
    private String phone;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetailEntity> orderDetailList = new ArrayList<>();
    
    public void addOrderDetail(OrderDetailEntity orderDetailEntity) {
    	if (orderDetailList == null) {
    		orderDetailList = new ArrayList<>();
    	}
        this.orderDetailList.add(orderDetailEntity);
    }
    
    public void recalcTotalPrice() {
	    // 총 가격 계산
		totalPrice = orderDetailList.stream().mapToInt(d -> d.getTotalPrice()).sum();
    }
    
    public OrderDTO of(MapperUtil mapperUtil) {
    	OrderDTO orderDTO = mapperUtil.map(this, OrderDTO.class);
    	orderDTO.setOrderDetailList(
			orderDetailList.stream()
				.map(d -> d.of(mapperUtil)).toList()
    	);
    	orderDTO.setUserNo(user.getUserNo());
    	
        // 배송 정보 필드 추가 ⬇⬇⬇
        orderDTO.setDeliveryName(this.deliveryName);
        orderDTO.setReceiver(this.receiver);
        orderDTO.setZipcode(this.zipcode);
        orderDTO.setAddress1(this.address1);
        orderDTO.setAddress2(this.address2);
        orderDTO.setPhone(this.phone);
    	return orderDTO;
    }
    
    @OneToOne
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;

}
