package com.pettact.api.user.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.code.repository.CommonCodeRepository;
import com.pettact.api.security.service.EmailService;
import com.pettact.api.security.util.VerificationCodeStore;
import com.pettact.api.user.dto.UserJoinDTO;
import com.pettact.api.user.dto.UserPatchDTO;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final CommonCodeRepository commonCodeRepository;
	private final PasswordEncoder passwordEncoder;
	private final VerificationCodeStore verificationCodeStore;
	private final EmailService emailService;
	
    public void join(UserJoinDTO dto) {
        
    	if (isEmailDuplicated(dto.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        String verifiedKey = "verified:" + dto.getUserEmail();
        boolean verified = verificationCodeStore.verifyCode(verifiedKey, "true");

        if (!verified) {
        	log.warn("이메일 인증 안된 사용자 가입 시도: {}", dto.getUserEmail());	// TODO: 통계 때문에 넣어둔거임
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        verificationCodeStore.remove(verifiedKey); // 인증 한 번만 사용하도록 제거
        
        if (isNicknameDuplicated(dto.getUserNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        
        // 권한
        CommonCode role = commonCodeRepository.findById("ROLE_USER")
            .orElseThrow(() -> new IllegalStateException("기본 권한 코드 없음"));
        
        // 상태
        CommonCode status = commonCodeRepository.findById("STATUS_ACTIVE")
            .orElseThrow(() -> new IllegalStateException("기본 상태 코드 없음"));
    	
        Users user = Users.builder()
                .userEmail(dto.getUserEmail())
                .userPassword(passwordEncoder.encode(dto.getUserPassword()))
                .userName(dto.getUserName())
                .userNickname(dto.getUserNickname())
                .userTel(dto.getUserTel())
                .userBirth(dto.getUserBirth())
                .userZipcode(dto.getUserZipcode())
                .userStreet1(dto.getUserStreet1())
                .userDetailAddress(dto.getUserDetailAddress())
                .userHasPet(false)
                .userEmailChecked(dto.getUserEmailChecked())
                .userBlacklist(false)
                .roleCode(role)
                .statusCode(status)
                .build();

        userRepository.save(user);
    }
    
    // 중복확인
    public boolean isEmailDuplicated(String userEmail) {
    	return userRepository.existsByUserEmail(userEmail);
    }

    // 중복확인
    public boolean isNicknameDuplicated(String userNickname) {
    	return userRepository.existsByUserNickname(userNickname);
    }
    
    // 이메일 찾기
    public String findEmailByNameAndTel(String userName, String userTel) {
        Users user = userRepository.findByUserNameAndUserTel(userName, userTel)
            .orElseThrow(() -> new IllegalArgumentException("일치하는 정보가 없습니다."));
        
        return user.getUserEmail();
    }
    
    // 비밀번호 재설정 메일 전송
    public void sendPasswordResetMail(String userEmail) {
        Users user = userRepository.findByUserEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 사용자가 없습니다."));

        String token = UUID.randomUUID().toString();
        verificationCodeStore.saveCode("password-reset-token:" + token, userEmail);

        emailService.sendPasswordResetLink(userEmail, token);
    }

    // 비밀번호 재설정
    public void updatePassword(String userEmail, String newPassword) {
        Users user = userRepository.findByUserEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        user.setUserPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 회원정보 수정(patch -> 수정할 항목만 수정)
    public void patchUserInfo(Long userNo, UserPatchDTO dto) {
        Users user = userRepository.findById(userNo)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        dto.getUserNickname().ifPresent(nickname -> {
            if (nickname.isBlank()) {
                throw new IllegalArgumentException("닉네임은 비어 있을 수 없습니다.");
            }

            user.setUserNickname(nickname);
        });
        
        dto.getUserTel().ifPresent(tel -> {
            if (!tel.matches("^01[016789]\\d{7,8}$")) {
                throw new IllegalArgumentException("전화번호 형식이 올바르지 않습니다.");
            }
            user.setUserTel(tel);
        });
        
        dto.getUserZipcode().ifPresent(user::setUserZipcode);
        dto.getUserStreet1().ifPresent(user::setUserStreet1);
        dto.getUserDetailAddress().ifPresent(user::setUserDetailAddress);

        userRepository.save(user);
    }
    
    // 회원 탈퇴
    public void withdrawUser(Long userNo) {
        Users user = userRepository.findById(userNo)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        CommonCode withdrawStatus = commonCodeRepository.findById("STATUS_WITHDRAW")
            .orElseThrow(() -> new IllegalStateException("탈퇴 상태 코드 없음"));

        user.setStatusCode(withdrawStatus);
        user.softDelete();

        userRepository.save(user);
    }
}