package com.pettact.api.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.user.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByUserEmail(String userEmail);
	
	boolean existsByUserEmail(String userEmail);
	
	boolean existsByUserNickname(String userNickname);

	Optional<Users> findByUserNameAndUserTel(String userName, String userTel);
	
	List<Users> findByIsDeletedFalse();
}
