package com.pettact.api.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.admin.dto.board.AdminBoardListDTO;
import com.pettact.api.admin.dto.dashboard.DashboardStatsDTO;
import com.pettact.api.admin.dto.dashboard.board.BoardCountCategoryDTO;
import com.pettact.api.admin.dto.dashboard.board.BoardDailyStatsDTO;
import com.pettact.api.admin.dto.dashboard.board.BoardStatsDTO;
import com.pettact.api.admin.dto.dashboard.report.ReportDailyStatsDTO;
import com.pettact.api.admin.dto.dashboard.user.DailyUserStatsDTO;
import com.pettact.api.admin.dto.dashboard.user.SellerPendingDTO;
import com.pettact.api.admin.dto.dashboard.user.UserRegStatsDTO;
import com.pettact.api.admin.service.AdminDashboardService;
import com.pettact.api.report.dto.ReportResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    // 대시보드 요약 통계
    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary() {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ResponseEntity.ok().body(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("대시보드 요약 조회 실패: " + e.getMessage());
        }
    }

    // --------------- USER ---------------
    // 가입자 통계
    @GetMapping("/user/registration")
    public ResponseEntity<?> getUserRegistrationStats() {
        try {
            UserRegStatsDTO stats = dashboardService.getUserRegStats();
            return ResponseEntity.ok().body(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("가입자 통계 조회 실패: " + e.getMessage());
        }
    }

    // 일별 가입자 통계 (차트용)
    @GetMapping("/user/daily")
    public ResponseEntity<?> getDailyUserStats(@RequestParam(value = "days", defaultValue = "7") int days) {
        try {
            List<DailyUserStatsDTO> stats = dashboardService.getDailyUserStats(days);
            return ResponseEntity.ok().body(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("일별 가입자 통계 조회 실패: " + e.getMessage());
        }
    }
    
    // --------------- BOARD ---------------
    // 게시물 통계
    @GetMapping("/board")
    public ResponseEntity<?> getBoardStats() {
        try {
            BoardStatsDTO stats = dashboardService.getBoardStats();
            return ResponseEntity.ok().body(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("게시물 통계 조회 실패: " + e.getMessage());
        }
    }

    // 카테고리별 게시글 비율
    @GetMapping("/board/category-ratio")
    public ResponseEntity<?> getBoardCategoryRatio() {
        try {
            List<BoardCountCategoryDTO> list = dashboardService.getBoardCountByCategory();
            return ResponseEntity.ok().body(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("카테고리별 게시글 통계 조회 실패: " + e.getMessage());
        }
    }
    
    // 일별 게시물 통계
    @GetMapping("/board/daily")
    public ResponseEntity<?> getDailyBoardStats(@RequestParam(value = "days", defaultValue = "7") int days) {
        try {
            List<BoardDailyStatsDTO> stats = dashboardService.getDailyBoardStats(days);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("일별 게시글 통계 조회 실패: " + e.getMessage());
        }
    }

    // 최근 삭제된 게시물 통계
    @GetMapping("/board/recent-deleted")
    public ResponseEntity<?> getRecentDeletedBoards(
    		@RequestParam(value = "todayOnly", required = false) Boolean todayOnly,
    		@RequestParam(value = "limit", defaultValue = "10") int limit) {
    	
    	try {
    		List<AdminBoardListDTO> list = dashboardService.getRecentDeletedBoards(todayOnly, limit);
    		return ResponseEntity.ok(list);
    	} catch (Exception e) {
    		return ResponseEntity.badRequest().body("최근 삭제된 게시물 조회 실패: " + e.getMessage());
    	}
    }

    // --------------- REPORT ---------------
    // 일별 신고 통계
    @GetMapping("/report/daily")
    public ResponseEntity<?> getDailyReportStats(@RequestParam(value = "days", defaultValue = "7") int days) {
        try {
            List<ReportDailyStatsDTO> stats = dashboardService.getDailyReportStats(days);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("일별 신고 통계 조회 실패: " + e.getMessage());
        }
    }
    
    // 미처리 신고 건수 조회
    @GetMapping("/report/pending-count")
    public ResponseEntity<?> getPendingReportCount() {
        try {
            Long count = dashboardService.getPendingReportCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("미처리 신고 건수 조회 실패: " + e.getMessage());
        }
    }

    // 최신 신고 리스트 조회
    @GetMapping("/report/latest")
    public ResponseEntity<?> getLatestReports(
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        try {
            List<ReportResponseDto> list = dashboardService.getLatestReports(limit);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("최근 신고 내역 조회 실패: " + e.getMessage());
        }
    }
    
    // --------------- SELLER ---------------
    // 판매자 승인 대기 수 
    @GetMapping("/seller/pending-count")
    public ResponseEntity<?> getPendingSellerCount() {
        try {
            Long count = dashboardService.getPendingSellerCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("판매자 승인 대기자 수 조회 실패: " + e.getMessage());
        }
    }

    @GetMapping("/seller/pending")
    public ResponseEntity<?> getPendingSellerList(
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        try {
            List<SellerPendingDTO> list = dashboardService.getPendingSellers(limit);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("판매자 승인 대기 목록 조회 실패: " + e.getMessage());
        }
    }
}
