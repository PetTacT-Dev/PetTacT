package com.pettact.api.order.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pettact.api.cart.entity.CartEntity;
import com.pettact.api.cart.repository.CartRepository;
import com.pettact.api.core.base.MapperUtil;
import com.pettact.api.order.dto.OrderDTO;
import com.pettact.api.order.dto.OrderDetailDTO;
import com.pettact.api.order.dto.OrderResponseDTO;
import com.pettact.api.order.entity.OrderDetailEntity;
import com.pettact.api.order.entity.OrderEntity;
import com.pettact.api.order.enums.OrderStatus;
import com.pettact.api.order.repository.OrderDetailRepository;
import com.pettact.api.order.repository.OrderRepository;
import com.pettact.api.product.entity.ProductEntity;
import com.pettact.api.product.repository.ProductRepository;
import com.pettact.api.user.entity.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final ProductRepository productRepository;
	private final MapperUtil mapperUtil;
	
	private final CartRepository cartRepository;
	
    // 유저의 모든 주문 조회 메서드 추가
	public Page<OrderDTO> getOrdersByUser(Users user, Pageable pageable) {
		Page<OrderEntity> ordersPage  = orderRepository.findByUserAndIsDeletedFalse(user, pageable);
		
	    return ordersPage.map(order -> order.of(mapperUtil));
	}
	
	// 주문 상세 정보
    private void addOrderDetail(OrderEntity orderEntity, List<OrderDetailDTO> list) {
        for (OrderDetailDTO dto : list) {
            ProductEntity product = productRepository.findById(dto.getProductNo())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            OrderDetailEntity detail = OrderDetailEntity.builder()
                    .order(orderEntity)
                    .product(product)
                    .productPrice(product.getProductPrice())  // 상품 가격은 DB에서 가져오고
                    .productStock(dto.getProductStock())      // ✅ 수량은 DTO에서 가져오기
                    .build();

            orderEntity.addOrderDetail(detail);
            orderDetailRepository.save(detail); // detail DB에 저장
        }

        // 총합 다시 계산
        orderEntity.recalcTotalPrice();
    }
	
	// 주문 등록
	@Transactional
	public OrderDTO createOrder(Users user, OrderDTO.CreateRequest request ) {
		// 1. 주문 정보 생성
		OrderEntity orderEntity = OrderEntity.builder()
				.user(user)
				.status(OrderStatus.COMPLETE)
				// 2. 배송 정보 입력 받기
				.deliveryName(request.getDeliveryName())
				.receiver(request.getReceiver())
				.zipcode(request.getZipcode())
				.address1(request.getAddress1())
				.address2(request.getAddress2())
				.phone(request.getPhone())
				.build();
		
	orderRepository.save(orderEntity);	
	
	// 3 . 최신 장바구니 수량으로 업데이트
	List<Long> productNos = request.getOrderDetails().stream()
			.map(OrderDetailDTO::getProductNo)
			.toList();
	
	List<CartEntity> cartList = cartRepository.findByUserAndProduct_ProductNoIn(user, productNos);
	
	for(OrderDetailDTO dto : request.getOrderDetails()) {
		for(CartEntity cart : cartList) {
			if(cart.getProduct().getProductNo().equals(dto.getProductNo())) {
				dto.setProductStock(cart.getProductStock());// 최신수량 업데이트
				break;
			}
		}
	}
	// 4 . 주문 상세 저장
	addOrderDetail(orderEntity, request.getOrderDetails());
	
	// 5 . 장바구니 삭제
	cartRepository.deleteByUserAndProduct_ProductNoIn(user, productNos);
	
	// 6 . 결과 반환 
	return orderEntity.of(mapperUtil);
	}


	// 주문 상세
	@Transactional(readOnly = true)
	public OrderResponseDTO getOrderDetail(Long orderNo, Users user) {
		OrderEntity order = orderRepository.findById(orderNo)
				.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
		
		if(!order.getUser().getUserNo().equals(user.getUserNo())) {
			throw new SecurityException("해당 주문에 접근할 수 없습니다.");
		}
		OrderResponseDTO dto = mapperUtil.map(order, OrderResponseDTO.class);
		dto.setOrderNo(order.getOrderNo());
		dto.setStatus(order.getStatus().name());
		
	    List<OrderDetailDTO> products = order.getOrderDetailList().stream()
	            .map(detail -> new OrderDetailDTO(
	            		detail.getOrder().getOrderNo(),
	                    detail.getOrderDetailId(),
	                    detail.getProduct().getProductNo(),
	                    detail.getProduct().getProductName(),
	                    detail.getProductStock(),
	                    detail.getProductPrice(),
	                    detail.getProduct().getImageUrl()
	            ))
	            .toList();
				
	    dto.setProducts(products);
		return dto;
	}
	
	// 주문 취소
	@Transactional
	public String cancelOrder(Long orderNo, Users user) {
	    OrderEntity order = orderRepository.findById(orderNo)
	            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

	    if (!order.getUser().getUserNo().equals(user.getUserNo())) {
	        throw new SecurityException("해당 주문을 취소할 권한이 없습니다.");
	    }

	    if (!order.getStatus().equals(OrderStatus.COMPLETE)) {
	        throw new IllegalStateException("진행 중인 주문만 취소할 수 있습니다.");
	    }

	    order.softDelete();
	    order.setStatus(OrderStatus.CANCELLED); // 주문 상태 변경

	    // ✅ 주문 상세(OrderDetail)도 함께 취소 처리
	    for (OrderDetailEntity detail : order.getOrderDetailList()) {
	    	detail.softDelete();
	        detail.setStatus(OrderStatus.CANCELLED);
	    }

	    orderRepository.save(order); // Cascade 설정 시 주문 상세도 함께 저장됨
	    return "주문 취소가 완료되었습니다.";
	}
}
