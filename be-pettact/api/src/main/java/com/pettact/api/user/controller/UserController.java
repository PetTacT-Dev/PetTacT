package com.pettact.api.user.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.security.service.EmailService;
import com.pettact.api.security.util.VerificationCodeStore;
import com.pettact.api.security.vo.CustomUserDetails;
import com.pettact.api.user.dto.EmailFindRequestDTO;
import com.pettact.api.user.dto.EmailFindResponseDTO;
import com.pettact.api.user.dto.PasswordResetDTO;
import com.pettact.api.user.dto.UserInfoDTO;
import com.pettact.api.user.dto.UserJoinDTO;
import com.pettact.api.user.dto.UserPatchDTO;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final VerificationCodeStore verificationCodeStore;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserJoinDTO dto) {
        try {
            userService.join(dto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }
    
    // 이메일 인증 요청(링크)
    @PostMapping("/email/send")
    public ResponseEntity<?> sendVerificationEmail(@RequestParam("userEmail") String userEmail){
    	String token = UUID.randomUUID().toString();
        verificationCodeStore.saveCode("email-token:" + token, userEmail);
        emailService.sendVerificationLink(userEmail, token);
        return ResponseEntity.ok("이메일 인증 링크를 전송했습니다.");
    }
    
    // 이메일 인증 링크 클릭
    @GetMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        String email = verificationCodeStore.getCode("email-token:" + token);

        if (email == null) {
            return ResponseEntity.badRequest().body("유효하지 않거나 만료된 인증 링크입니다.");
        }

        verificationCodeStore.saveCode("verified:" + email, "true");
        verificationCodeStore.remove("email-token:" + token);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }
    
    // 현재 로그인된 사용자 정보 조회(이건 AccessToken 사용)
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails){
    	if(userDetails == null) {
    		return ResponseEntity.status(401).body("로그인이 필요합니다.");
    	}
    	
    	Users user = userDetails.getUserEntity();

        Map<String, Object> result = Map.of(
                "userEmail", user.getUserEmail(),
                "userNo", user.getUserNo(),
                "userNickname", user.getUserNickname(),
                "userRole", user.getRoleCode()
        );
        
        return ResponseEntity.ok(result);
    }
    
    // 이메일 찾기
    @PostMapping("/email/find")
    public ResponseEntity<?> findEmail(@RequestBody EmailFindRequestDTO dto) {
        try {
            String userEmail = userService.findEmailByNameAndTel(dto.getUserName(), dto.getUserTel());
            return ResponseEntity.ok(new EmailFindResponseDTO(userEmail));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 비밀번호 재설정 메일 전송
    @PostMapping("/password/send")
    public ResponseEntity<?> sendPasswordResetMail(@RequestParam("userEmail") String userEmail){
    	userService.sendPasswordResetMail(userEmail);
    	return ResponseEntity.ok("비밀번호 재설정 링크를 이메일로 전송했습니다.");
    }
    
    // 비밀번호 재설정 링크 클릭시 토큰 확인
    @GetMapping("/password/verify")
    public ResponseEntity<?> verifyPasswordResetToken(@RequestParam("token") String token) {    	
    	String email = verificationCodeStore.getCode("password-reset-token:" + token);

        if (email == null) {
            return ResponseEntity.badRequest().body("유효하지 않거나 만료된 링크입니다.");
        }

        verificationCodeStore.saveCode("verified:" + email, "true");
        verificationCodeStore.remove("password-reset-token:" + token);
        
        // 프론트에서 이걸 가지고 비밀번호 재설정 페이지로 연결되게
        return ResponseEntity.ok(Map.of("email", email, "message", "인증 성공"));
    }
    
    // 비밀번호 재설정
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO resetDTO) {
    	String email = verificationCodeStore.getCode("verified:" + resetDTO.getUserEmail());

    	if (email == null) {
    		return ResponseEntity.badRequest().body("유효하지 않거나 만료된 링크입니다.");
    	}
    	
    	userService.updatePassword(resetDTO.getUserEmail(), resetDTO.getNewPassword());
    	
    	verificationCodeStore.remove(email);
    	
    	return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
    
    // 마이페이지용 내 정보 조회
    @GetMapping("/me/detail")
    public ResponseEntity<?> getUserDetail(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Users user = userDetails.getUserEntity();

        UserInfoDTO dto = UserInfoDTO.builder()
            .userEmail(user.getUserEmail())
            .userName(user.getUserName())
            .userBirth(user.getUserBirth())
            .userNickname(user.getUserNickname())
            .userTel(user.getUserTel())
            .userZipcode(user.getUserZipcode())
            .userStreet1(user.getUserStreet1())
            .userDetailAddress(user.getUserDetailAddress())
            .build();

        return ResponseEntity.ok(dto);
    }
    
    // 정보 수정
    @PatchMapping("/update")
    public ResponseEntity<?> updateUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
    										@RequestBody @Valid UserPatchDTO userPatchDTO){
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        
        try {
        	userService.patchUserInfo(userDetails.getUserEntity().getUserNo(), userPatchDTO);
            return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 탈퇴
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        
        try {
        	userService.withdrawUser(userDetails.getUserEntity().getUserNo());
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
