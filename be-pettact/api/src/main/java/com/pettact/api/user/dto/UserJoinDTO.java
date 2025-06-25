package com.pettact.api.user.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinDTO {
	
    @NotBlank
    @Email
    private String userEmail;

    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$", message = "비밀번호는 영문자, 숫자, 특수문자를 포함해야 합니다.")
    private String userPassword;

    @NotBlank
    private String userName;

    @NotBlank
    private String userNickname;

    @NotBlank
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String userTel;

    @NotNull
    private LocalDate userBirth;

    private String userZipcode;
    private String userStreet1;
    private String userDetailAddress;

    @NotNull
    private Boolean userHasPet;

    @NotNull
    private Boolean userEmailChecked;
}
