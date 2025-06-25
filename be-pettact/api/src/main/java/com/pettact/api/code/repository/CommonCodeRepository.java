package com.pettact.api.code.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.code.entity.CommonCode;

public interface CommonCodeRepository extends JpaRepository<CommonCode, String>{
	List<CommonCode> findByGroup_GroupCodeOrderByCodeOrder(String groupCode);
}
