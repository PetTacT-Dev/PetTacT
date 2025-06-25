package com.pettact.api.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.code.entity.GroupCode;

public interface GroupCodeRepository extends JpaRepository<GroupCode, String>{
	
}
