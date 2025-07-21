package com.pettact.api.admin.service;

import static com.pettact.api.admin.util.DateRangeUtil.endOfLast7Days;
import static com.pettact.api.admin.util.DateRangeUtil.endOfThisMonthUntilToday;
import static com.pettact.api.admin.util.DateRangeUtil.endOfToday;
import static com.pettact.api.admin.util.DateRangeUtil.endOfYesterday;
import static com.pettact.api.admin.util.DateRangeUtil.startOfLast7Days;
import static com.pettact.api.admin.util.DateRangeUtil.startOfThisMonth;
import static com.pettact.api.admin.util.DateRangeUtil.startOfToday;
import static com.pettact.api.admin.util.DateRangeUtil.startOfYesterday;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pettact.api.admin.dto.board.AdminBoardListDTO;
import com.pettact.api.admin.dto.dashboard.DashboardStatsDTO;
import com.pettact.api.admin.dto.dashboard.board.BoardCountCategoryDTO;
import com.pettact.api.admin.dto.dashboard.board.BoardDailyStatsDTO;
import com.pettact.api.admin.dto.dashboard.board.BoardStatsDTO;
import com.pettact.api.admin.dto.dashboard.report.ReportDailyStatsDTO;
import com.pettact.api.admin.dto.dashboard.user.DailyUserStatsDTO;
import com.pettact.api.admin.dto.dashboard.user.SellerPendingDTO;
import com.pettact.api.admin.dto.dashboard.user.UserRegStatsDTO;
import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.report.Repository.ReportRepository;
import com.pettact.api.report.dto.ReportResponseDto;
import com.pettact.api.report.entity.Report;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
	private final UserRepository userRepository;
	private final BoardRepository boardRepository;
	private final ReportRepository reportRepository;
	
    // --------------- USER ---------------
	// 전체 사용자 통계
	public DashboardStatsDTO getDashboardStats() {
		// 날짜 계산용
	    long today = userRepository.countUsersBetween(startOfToday(), endOfToday());
	    long yesterday = userRepository.countUsersBetween(startOfYesterday(), endOfYesterday());
	    long weekly = userRepository.countUsersBetween(startOfLast7Days(), endOfLast7Days());
	    long monthly = userRepository.countUsersBetween(startOfThisMonth(), endOfThisMonthUntilToday());

        // 사용자 통계
        long totalUsers = userRepository.countTotalUsers();
        long activeUsers = userRepository.countActiveUsers();
        long totalSellers = userRepository.countActiveSellers();
        long pendingSellers = userRepository.countPendingSellers();

        // 증가율 계산
        double growthRate = (yesterday == 0) ? 100.0 : ((double)(today - yesterday) / yesterday) * 100;

        return DashboardStatsDTO.of(totalUsers, activeUsers, totalSellers, pendingSellers,
        								today, weekly, monthly, yesterday);
	}
	
	// 사용자 가입 통계
    public UserRegStatsDTO getUserRegStats() {
        long today = userRepository.countUsersBetween(startOfToday(), endOfToday());
        long yesterday = userRepository.countUsersBetween(startOfYesterday(), endOfYesterday());
        long weekly = userRepository.countUsersBetween(startOfLast7Days(), endOfLast7Days());
        long monthly = userRepository.countUsersBetween(startOfThisMonth(), endOfThisMonthUntilToday());

        return UserRegStatsDTO.of(today, yesterday, weekly, monthly);
    }
    
    // 차트 표시용 사용자 통계(
    public List<DailyUserStatsDTO> getDailyUserStats(int days) {
        List<DailyUserStatsDTO> statsList = new ArrayList<>();
        long runningTotal = 0;

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end;

            if (date.isEqual(LocalDate.now())) {
            	end = LocalDateTime.now();
            } else {
            	end = date.atTime(LocalTime.MAX);
            }
            long newUsers = userRepository.countUsersBetween(start, end);
            runningTotal += newUsers;

            statsList.add(DailyUserStatsDTO.of(date, newUsers, runningTotal));
        }

        return statsList;
    }

    // --------------- BOARD ---------------
    // 게시물 통계
    public BoardStatsDTO getBoardStats() {
        long total = boardRepository.countTotalBoards();
        long newToday = boardRepository.countDailyNewBoards();
        long deletedToday = boardRepository.countDailyDeletedBoards();

        return BoardStatsDTO.of(total, newToday, deletedToday);
    }

    // 카테고리별 게시글 비율
    public List<BoardCountCategoryDTO> getBoardCountByCategory() {
        List<Board> boards = boardRepository.findAllWithCategory();

        Map<String, Long> grouped = boards.stream()
            .collect(Collectors.groupingBy(
                b -> b.getBoardCategory().getBoardCategoryTitle(),
                Collectors.counting()
            ));

        return grouped.entrySet().stream()
            .map(e -> BoardCountCategoryDTO.from(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }
    
    // 일별 게시물 통계
    public List<BoardDailyStatsDTO> getDailyBoardStats(int days) {
        List<BoardDailyStatsDTO> statsList = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startDate = date.atStartOfDay();
            LocalDateTime endDate;

            if (date.isEqual(LocalDate.now())) {
                endDate = LocalDateTime.now();
            } else {
                endDate = date.atTime(LocalTime.MAX);
            }
            Long count = boardRepository.countBoardsBetween(startDate, endDate);

            statsList.add(BoardDailyStatsDTO.of(date, count));
        }

        return statsList;
    }
    
    // 최근 삭제된 게시물 통계
    public List<AdminBoardListDTO> getRecentDeletedBoards(Boolean todayOnly, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Board> boards;

        if (Boolean.TRUE.equals(todayOnly)) {
            boards = boardRepository.findTodayDeletedBoards(pageable);
        } else {
            boards = boardRepository.findRecentDeletedBoards(pageable);
        }

        return boards.stream()
            .map(AdminBoardListDTO::from)
            .collect(Collectors.toList());
    }
    
    // --------------- REPORT ---------------
    // 일별 신고 통계
    public List<ReportDailyStatsDTO> getDailyReportStats(int days) {
        List<ReportDailyStatsDTO> statsList = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startDate = date.atStartOfDay();
            LocalDateTime endDate;

            if (date.isEqual(LocalDate.now())) {
                endDate = LocalDateTime.now();
            } else {
                endDate = date.atTime(LocalTime.MAX);
            }
            
            Long count = reportRepository.countReportsBetween(startDate, endDate);

            statsList.add(ReportDailyStatsDTO.of(date, count));
        }

        return statsList;
    }
    
    // 미처리 신고 개수 조회
    public Long getPendingReportCount() {
        return reportRepository.countByStatus(0); // 0: 미처리
    }

    // 최신 신고 리스트 조회
    public List<ReportResponseDto> getLatestReports(int limit) {
        List<Report> reports = reportRepository.findLatestReports(limit);
        return reports.stream()
            .map(ReportResponseDto::fromEntity)
            .collect(Collectors.toList());
    }
    
    // --------------- SELLER ---------------
    // 판매자 승인 대기 수 
    public Long getPendingSellerCount() {
        return userRepository.countPendingSellers();
    }
    
    // 판매자 승인 대기 리스트 - 요약
    public List<SellerPendingDTO> getPendingSellers(int limit) {
        return userRepository.findTopPendingSellers(limit).stream()
            .map(u -> new SellerPendingDTO(
                u.getUserNo(),
                u.getUserEmail(),
                u.getUserName(),
                u.getUserNickname(),
                u.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }
}
