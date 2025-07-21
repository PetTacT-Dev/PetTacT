package com.pettact.api.report.service;

import com.pettact.api.admin.dto.report.AdminReportListDTO;
import com.pettact.api.common.dto.PageResponseDto;
import com.pettact.api.notification.dto.NotificationReqDTO;
import com.pettact.api.notification.enums.NotificationType;
import com.pettact.api.notification.enums.TargetType;
import com.pettact.api.notification.service.NotificationService;
import com.pettact.api.report.Repository.ReportRepository;
import com.pettact.api.report.dto.ReportCreateDto;
import com.pettact.api.report.dto.ReportResponseDto;
import com.pettact.api.report.entity.Report;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;


    // user
    @Transactional
    public ReportResponseDto submitReport(ReportCreateDto createDto, Long userNo, String clientIP) {

        Users users = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (createDto.getReportTargetNo() == null || createDto.getReportTargetNo()<=0 ||
                createDto.getReportTargetLocation() == null) {
            throw new IllegalArgumentException("신고 대상을 찾을 수 없습니다.");
        }

        boolean duplicateReport = reportRepository.isDuplicate
                (userNo, createDto.getReportTargetLocation(), createDto.getReportTargetNo());
        if (duplicateReport) {
            throw new IllegalArgumentException("이미 신고한 대상입니다. 신고는 한번만 가능합니다.");
        }

        Report report = createDto.toEntity(users);
        report.setReportIP(clientIP);
        Report savedReport = reportRepository.save(report);
        return ReportResponseDto.fromEntity(savedReport);
    }

    public ReportResponseDto getMyReport(Long reportNo, Long userNo) {
        Report report = reportRepository.findById(reportNo)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다."));

        if(!report.getUsers().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("본인의 신고 내역만 조회할 수 있습니다.");
        }
        return ReportResponseDto.fromEntity(report);
    }

    public List<ReportResponseDto> getListReport(Long userNo) {
        List<Report> reports = reportRepository.findByReports(userNo);

        return reports.stream()
                .map(ReportResponseDto::fromEntity)
                .collect(Collectors.toList());
    }


    // admin
    public PageResponseDto<AdminReportListDTO> getAdminListReport(
    	    Report.ReportTargetLocation reportTargetLocation, Integer reportStatus, Report.ReportReason reportReason,
    	    LocalDate startDate, LocalDate endDate, int page, int size
	) {
	    Pageable pageable = PageRequest.of(page - 1, size);
	    Page<Report> reports = reportRepository.findAllWithFilters(
	        reportTargetLocation, reportStatus, reportReason, startDate, endDate, pageable
	    );

	    List<AdminReportListDTO> dtoList = reports.getContent().stream()
	        .map(AdminReportListDTO::from)
	        .toList();

	    return new PageResponseDto<>(dtoList, (int) reports.getTotalElements(), page, size);
	}


    public ReportResponseDto getReportDetail(Long reportNo) {
        Report report = reportRepository.findById(reportNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 번호의 신고를 찾을 수 없습니다."));
        return ReportResponseDto.fromEntity(report);
    }

    @Transactional
    public ReportResponseDto updateReport(Long reportNo, Integer status, Long adminUserNo) {

        Report report = reportRepository.findById(reportNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 번호의 신고를 찾을 수 없습니다."));
        if (status <0 || status > 2) {
            throw new IllegalArgumentException("올바르지 않은 상태값입니다.");
        }
        report.setReportStatus(status);
        Report savedReport = reportRepository.save(report);
        
        NotificationReqDTO dto = NotificationReqDTO.of(
                adminUserNo,
                report.getUsers().getUserNo(),
                NotificationType.REPORT_RESULT,
                report.getReportNo(),
                TargetType.REPORT,
                "신고 처리 결과 안내",
                getReportStatusMessage(status, report)
            );

            notificationService.sendNotification(dto);
        
        return ReportResponseDto.fromEntity(savedReport);
    }

    private String getReportStatusMessage(Integer status, Report report) {
        String targetType = report.getReportTargetLocation().name();
        switch (status) {
            case 1:
                return "회원님이 신고하신 [" + targetType + "]이(가) 승인 처리되었습니다.";
            case 2:
                return "회원님이 신고하신 [" + targetType + "]은(는) 검토 결과 반려되었습니다.";
            default:
                return "회원님이 신고하신 [" + targetType + "]의 상태가 변경되었습니다.";
        }
    }
    
    @Transactional
    public void deleteReport(Long reportNo) {

        Report report = reportRepository.findById(reportNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 번호의 신고를 찾을 수 없습니다."));
        if (report.getReportStatus() != null && report.getReportStatus() == 0) {
            throw new IllegalStateException("처리 중인 신고는 삭제할 수 없습니다.");
        }
        reportRepository.deleteById(reportNo);
    }

}
