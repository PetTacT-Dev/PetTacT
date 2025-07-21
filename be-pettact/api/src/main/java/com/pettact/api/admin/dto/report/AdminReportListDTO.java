package com.pettact.api.admin.dto.report;

import java.time.LocalDateTime;

import com.pettact.api.board.entity.Board;
import com.pettact.api.report.entity.Report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AdminReportListDTO {
    private Long reportNo;
    private Long reportTargetNo;
    private String reportTargetLocation;
    private Long userNo;
    private String userEmail;
    private String userNickname;
    private String reportReason;
    private String reportDescription;
    private Integer reportStatus;
    private LocalDateTime createdAt;

    public static AdminReportListDTO from(Report report) {
        return AdminReportListDTO.builder()
                .reportNo(report.getReportNo())
                .reportTargetNo(report.getReportTargetNo())
                .reportTargetLocation(report.getReportTargetLocation().toString())
                .userNo(report.getUsers().getUserNo())
                .userEmail(report.getUsers().getUserEmail())
                .userNickname(report.getUsers().getUserNickname())
                .reportReason(report.getReportReason().getDescription())
                .reportDescription(report.getReportDescription())
                .reportStatus(report.getReportStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
}