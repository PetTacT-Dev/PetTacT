package com.pettact.api.security.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pettact.api.security.vo.CustomUserDetails;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
	
	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.info("로그인 시도 이메일: {}", email);

		Users user = userRepository.findByUserEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

		if (user.isDeleted()) {
			throw new UsernameNotFoundException("이미 탈퇴한 사용자입니다: " + email);
		}

		log.info("사용자 정보 로드 완료: {}", user.getUserNickname());

		return new CustomUserDetails(user);
	}
}
