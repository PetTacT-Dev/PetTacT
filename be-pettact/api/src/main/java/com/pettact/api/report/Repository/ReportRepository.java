package com.pettact.api.report.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pettact.api.report.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT COUNT(r) > 0 FROM Report r WHERE r.users.userNo = :userNo AND r.reportTargetLocation = :reportTargetLocation AND r.reportTargetNo = :targetNo")
    boolean isDuplicate(@Param("userNo") Long userNo,
                        @Param("reportTargetLocation") Report.ReportTargetLocation reportTargetLocation,
                        @Param("targetNo") Long reportTargetNo);

    @Query("SELECT r FROM Report r WHERE r.users.userNo = :userNo ORDER BY r.createdAt DESC")
    List<Report> findByReports(@Param("userNo") Long userNo);

    @Query("""
    	    SELECT r FROM Report r
    	    WHERE (:reportTargetLocation IS NULL OR r.reportTargetLocation = :reportTargetLocation)
    	      AND (:reportStatus IS NULL OR r.reportStatus = :reportStatus)
    	      AND (:reportReason IS NULL OR r.reportReason = :reportReason)
    	      AND (:startDate IS NULL OR r.createdAt >= :startDate)
    	      AND (:endDate IS NULL OR r.createdAt <= :endDate)
    	    ORDER BY r.createdAt DESC
    	""")
	Page<Report> findAllWithFilters(
	    @Param("reportTargetLocation") Report.ReportTargetLocation reportTargetLocation,
	    @Param("reportStatus") Integer reportStatus,
	    @Param("reportReason") Report.ReportReason reportReason,
	    @Param("startDate") LocalDate startDate,
	    @Param("endDate") LocalDate endDate,
	    Pageable pageable
	);


    @Query("SELECT COUNT(r) FROM Report r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    Long countReportsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // 미처리 상태 신고 건수
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportStatus = :status")
    Long countByStatus(@Param("status") Integer status);

    // 최신 신고 리스트 조회
    @Query("SELECT r FROM Report r ORDER BY r.createdAt DESC")
    List<Report> findTopNReports(Pageable pageable);

    // Pageable 기반 조회를 쉽게 쓰기 위한 default 메서드
    default List<Report> findLatestReports(int limit) {
        return findTopNReports(PageRequest.of(0, limit));
    }
    
    
}
