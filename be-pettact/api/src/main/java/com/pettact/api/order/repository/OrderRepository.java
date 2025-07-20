package com.pettact.api.order.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.order.entity.OrderEntity;
import com.pettact.api.user.entity.Users;

// 주문
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
	List<OrderEntity> findByUser_UserNo(Long userNo);
	List<OrderEntity> findByUser(Users user);
	
	Page<OrderEntity> findByUserAndIsDeletedFalse(Users user, Pageable pageable);
	
	Optional<OrderEntity> findByOrderNo(Long orderNo);

	
}
