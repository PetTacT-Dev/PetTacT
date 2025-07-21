package com.pettact.api.admin.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.admin.dto.board.AdminBoardDetailDTO;
import com.pettact.api.admin.dto.board.AdminBoardListDTO;
import com.pettact.api.admin.dto.report.AdminReportListDTO;
import com.pettact.api.admin.dto.user.AdminUserDetailDTO;
import com.pettact.api.admin.dto.user.AdminUserListDTO;
import com.pettact.api.admin.service.AdminService;
import com.pettact.api.code.service.CommonCodeService;
import com.pettact.api.common.dto.PageResponseDto;
import com.pettact.api.report.dto.ReportResponseDto;
import com.pettact.api.report.entity.Report;
import com.pettact.api.report.service.ReportService;
import com.pettact.api.security.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/v1/admin")
public class AdminController {
	private final AdminService adminService;
	private final CommonCodeService commonCodeService;
	private final ReportService reportService;

	// 관리자 responseEntity 응답 형식 -> 에러 출력 e.getMessage() 로 처리
	
	// 회원 관리
	// 회원 목록
	@GetMapping("/user")
	public ResponseEntity<?> getUserList(
	        @RequestParam(value = "keyword", required = false) String keyword,
	        @RequestParam(value = "status", required = false) String status,
	        @RequestParam(value = "role", required = false) String role,
	        @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
	        @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
	    try {
	        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
	        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59) : null;

	        PageResponseDto<AdminUserListDTO> result = adminService.getUserList(keyword, status, role, startDateTime, endDateTime, page, size);
	        return ResponseEntity.ok(result);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("회원 목록을 불러오는 중 오류: " + e.getMessage());
	    }
	}

	
	// 필터 옵션들
	@GetMapping("/user/list/filters")
	public ResponseEntity<?> getFilterOptions() {
	    try {
	        Map<String, Object> filterOptions = new HashMap<>();

	        filterOptions.put("statusOptions", commonCodeService.getCodeOptions("USER_STATUS"));
	        filterOptions.put("roleOptions", commonCodeService.getCodeOptions("USER_ROLE"));

	        return ResponseEntity.ok().body(filterOptions);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().
	        		body("필터 옵션 조회 실패" + e.getMessage());
	    }
	}
	
	// 회원 정보 상세보기
	@GetMapping("/user/{userNo}")
	public ResponseEntity<?> getUserDetail(@PathVariable("userNo") Long userNo){
		try {
			AdminUserDetailDTO userDetail = adminService.getUserDetail(userNo);
			return ResponseEntity.ok().body(userDetail);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("회원 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
	
	// 회원 잠금(status_code 변경)
	@PostMapping("/user/{userNo}/block")
	public ResponseEntity<?> lockUser(@PathVariable("userNo") Long userNo){
	    try {
	        boolean result = adminService.lockUserByUserNo(userNo);

	        if(result) {
	            return ResponseEntity.ok().body("해당 회원이 잠금 처리되었습니다.");
	        } else {
	            return ResponseEntity.badRequest().body("이미 잠금 상태이거나 처리할 수 없습니다.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("회원 잠금 기능 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}
	
	// 회원 잠금 해제
	@PostMapping("/user/{userNo}/unblock")
	public ResponseEntity<?> unlockUser(@PathVariable("userNo") Long userNo){
	    try {
	        boolean result = adminService.unlockUserByUserNo(userNo);

	        if(result) {
	            return ResponseEntity.ok().body("해당 회원이 잠금 해제 처리되었습니다.");
	        } else {
	            return ResponseEntity.badRequest().body("이미 잠금 해제 상태이거나 처리할 수 없습니다.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("회원 잠금 해제 기능 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}
	
	// TODO: 블랙리스트 처리 -> 신고 내역 기반으로 처리
	
	// 판매자 관리
	// 판매자 목록 조회
	@GetMapping("/seller")
	public ResponseEntity<?> getSellerList(
	        @RequestParam(value = "keyword", required = false) String keyword,
	        @RequestParam(value = "status", required = false) String status,
	        @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
	        @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size) {
	    
	    try {
	        // LocalDate -> LocalDateTime 변환
	        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
	        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;
	        
	        PageResponseDto<AdminUserListDTO> sellerList = adminService.getSellerList(keyword, status, startDateTime, endDateTime, page, size);
	        return ResponseEntity.ok().body(sellerList);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest()
	            .body("판매자 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}
	
	// TODO: 판매자 권한 요청 대기중 목록
	@GetMapping("/seller/requests")
	public ResponseEntity<?> getSellerRequests(
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
	    try {
	    	PageResponseDto<AdminUserListDTO> requests = adminService.getSellerRequests(page, size);
	        return ResponseEntity.ok().body(requests);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest()
	            .body("판매자 권한 요청 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}
	
	// 판매자 권한 승인(ROLE_USER -> ROLE_SELLER)
	@PatchMapping("/seller/{userNo}/approve")
	public ResponseEntity<?> approveSeller(@PathVariable("userNo") Long userNo,
	                                       @AuthenticationPrincipal CustomUserDetails adminUser){
		try {
			boolean result = adminService.approveSellerByUserNo(userNo, adminUser.getUserEntity().getUserNo());

	        if (result) {
	            return ResponseEntity.ok().body("판매자 권한이 성공적으로 승인되었습니다.");
	        } else {
	            return ResponseEntity.badRequest().body("이미 판매자 권한을 가진 사용자입니다.");
	        }
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body("판매자 권한 승인 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
	
	// 판매자 활동 내역 조회
//	@GetMapping("/seller/{userNo}/activity")
//	public ResponseEntity<?> getSellerActivity(@PathVariable("userNo") Long userNo) {
//	    try {
//	        SellerActivityDTO activity = adminService.getSellerActivity(userNo);
//	        return ResponseEntity.ok().body(activity);
//	    } catch (Exception e) {
//	        return ResponseEntity.badRequest()
//	            .body("판매자 활동 내역 조회 중 오류가 발생했습니다: " + e.getMessage());
//	    }
//	}
	
	// 게시물 관리
	// 게시물 목록 조회
	@GetMapping("/board")
	public ResponseEntity<?> getBoardList(
		    @RequestParam(value = "keyword", required = false) String keyword,
		    @RequestParam(value = "category", required = false) Long categoryNo,
		    @RequestParam(value = "isDeleted", required = false) Boolean isDeleted,
		    @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
		    @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size
    ) {	    
		    try {
		        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;
		        
		        PageResponseDto<AdminBoardListDTO> boardList = adminService.getBoardList(keyword, categoryNo, isDeleted,
		        																		startDateTime, endDateTime,
		        																		page, size);
		        
		        return ResponseEntity.ok().body(boardList);
		    } catch (Exception e) {
		        return ResponseEntity.badRequest().body("게시물 목록 조회 중 오류: " + e.getMessage());
		    }
		}
	
	// 게시물 상세보기
	@GetMapping("/board/{boardNo}")
	public ResponseEntity<?> getBoardDetail(@PathVariable("boardNo") Long boardNo) {
	    try {
	        AdminBoardDetailDTO boardDetail = adminService.getBoardDetail(boardNo);
	        return ResponseEntity.ok().body(boardDetail);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("게시물 상세 조회 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}
	
	// 판매자 활동 내역 조회

	
	// TODO: 게시물 삭제 처리
	
	// 신고 목록 조회
	@GetMapping("/report")
	public ResponseEntity<PageResponseDto<AdminReportListDTO>> getListReport(
			@RequestParam(value = "location", required = false) Report.ReportTargetLocation location,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "reason", required = false) Report.ReportReason reason,
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
		PageResponseDto<AdminReportListDTO> responseDto 
				= reportService.getAdminListReport(location, status, reason, startDate, endDate, page, size);
		return ResponseEntity.ok(responseDto);
	}

	// 신고 내역 상세 조회
	@GetMapping("/report/{reportNo}")
	public ResponseEntity<ReportResponseDto> getReportDetail(@PathVariable("reportNo") Long reportNo) {
		ReportResponseDto responseDto = reportService.getReportDetail(reportNo);
		return ResponseEntity.ok(responseDto);
	}

	// 신고 상태 변경
	@PatchMapping("/report/{reportNo}/status")
	public ResponseEntity<ReportResponseDto> updateReportStatus(@PathVariable("reportNo") Long reportNo, @RequestParam("status") Integer status,
																@AuthenticationPrincipal CustomUserDetails adminUser) {
		ReportResponseDto responseDto = reportService.updateReport(reportNo, status, adminUser.getUserEntity().getUserNo());
		return ResponseEntity.ok(responseDto);
	}

	// 신고 삭제
	@DeleteMapping("/report/{reportNo}")
	public ResponseEntity<Void> deleteReport(@PathVariable("reportNo") Long reportNo) {
		reportService.deleteReport(reportNo);
		return ResponseEntity.noContent().build();
	}

}
