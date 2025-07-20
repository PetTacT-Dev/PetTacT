package com.pettact.api.admin.dto.dashboard.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SellerPendingDTO {
    private Long userNo;
    private String userEmail;
    private String userName;
    private String userNickname;
    private LocalDateTime createdAt;
}
