package com.pettact.api.user.dto;

import java.time.LocalDate;

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
public class UserInfoDTO {
    private String userEmail;
    private String userName;
    private String userNickname;
    private LocalDate userBirth;
    private String userTel;
    private String userZipcode;
    private String userStreet1;
    private String userDetailAddress;
//    private Boolean hasPet; TODO:직접 수정?
}
