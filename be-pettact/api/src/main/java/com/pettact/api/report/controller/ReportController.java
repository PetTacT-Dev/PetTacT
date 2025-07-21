package com.pettact.api.report.controller;

import com.pettact.api.report.dto.ReportCreateDto;
import com.pettact.api.report.dto.ReportResponseDto;
import com.pettact.api.report.entity.Report;
import com.pettact.api.report.service.ReportService;
import com.pettact.api.security.vo.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * user
     * ! url 주소는 해당 사항에 맞게 변경
     * 1. 신고 신청
     * 2. 상세 신고 내역 조획
     * 3. my 신고 list
     * @param createDto
     * @return
     */

    @PostMapping
    public ResponseEntity<ReportResponseDto> submitReport(@RequestBody ReportCreateDto createDto, @AuthenticationPrincipal CustomUserDetails userDetails,
                                                          HttpServletRequest request) {
        Long userNo = userDetails.getUser().getUserNo();
        String clientIP = getClientIP(request);
        ReportResponseDto responseDto = reportService.submitReport(createDto, userNo, clientIP);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // X-Forwarded-For에 여러 IP가 있을 경우 첫 번째가 실제 클라이언트 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    @GetMapping("/my/{reportNo}")
    public ResponseEntity<ReportResponseDto> getMyReport(@PathVariable Long reportNo,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUser().getUserNo();
        ReportResponseDto responseDto = reportService.getMyReport(reportNo, userNo);
        return ResponseEntity.ok(responseDto);
    }


    @GetMapping("/my")
    public ResponseEntity<List<ReportResponseDto>> getListReport(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUser().getUserNo();
        List<ReportResponseDto> responseDto = reportService.getListReport(userNo);
        return ResponseEntity.ok(responseDto);
    }
    
    @GetMapping("/reasons")
    public ResponseEntity<List<Map<String, String>>> getReportReasons() {
        List<Map<String, String>> result = Arrays.stream(Report.ReportReason.values())
            .map(r -> Map.of(
                "code", r.name(),
                "description", r.getDescription()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/locations")
    public List<Map<String, String>> getReportTargetLocations() {
        return Arrays.stream(Report.ReportTargetLocation.values())
                .map(loc -> Map.of(
                        "code", loc.name(),
                        "label", getKoreanLabel(loc)
                ))
                .collect(Collectors.toList());
    }
    
    private String getKoreanLabel(Report.ReportTargetLocation location) {
        return switch (location) {
            case BOARD -> "게시글";
            case REPLY -> "댓글";
            case PRODUCT -> "상품";
            case CART -> "장바구니";
            case ORDER -> "주문";
            case PET -> "동물";
            case USER -> "회원";
        };
    }
    
    /**
     * admin
     * -> 추후 도메인 변경 고려 plz,,
     * 1. 신고 list 조회
     * 2. 신고 {reportNo} 조회
     * 3. 신고 처리 -> status change
     * 4. 신고 삭제
     */



}
