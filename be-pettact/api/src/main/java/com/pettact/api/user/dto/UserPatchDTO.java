package com.pettact.api.user.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPatchDTO {
	
	// 회원이 수정할 수 있는 정보
	// userPassword - 비밀번호 변경은 따로 하는게 나은지
	// userNickname - 다시 중복확인
	// userTel - 다시 인증이 필요한데 아직 휴대폰 인증을 구현안함 / 쇼핑몰 판매,구매할 때 인증하게 할지?
	// userZipcode, userStreet1, userDetailAddress 수정가능
	
    private Optional<String> userNickname = Optional.empty();
    private Optional<String> userTel = Optional.empty();
    private Optional<String> userZipcode = Optional.empty();
    private Optional<String> userStreet1 = Optional.empty();
    private Optional<String> userDetailAddress = Optional.empty();
}
