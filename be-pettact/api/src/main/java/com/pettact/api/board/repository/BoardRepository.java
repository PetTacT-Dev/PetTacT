package com.pettact.api.board.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pettact.api.board.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	
	// 필터링,검색 기능 포함한 게시물 목록 조회

	@Query("""
	    SELECT b FROM Board b
	    WHERE (:keyword IS NULL OR b.boardTitle LIKE %:keyword%)
	    AND (:categoryNo IS NULL OR b.boardCategory.boardCategoryNo = :categoryNo)
	    AND (:isDeleted IS NULL OR b.isDeleted = :isDeleted)
	    AND (:startDateTime IS NULL OR b.createdAt >= :startDateTime)
	    AND (:endDateTime IS NULL OR b.createdAt <= :endDateTime)
	""")
	Page<Board> findBoardsWithFilters(
	    @Param("keyword") String keyword,
	    @Param("categoryNo") Long categoryNo,
	    @Param("isDeleted") Boolean isDeleted,
	    @Param("startDateTime") LocalDateTime startDateTime,
	    @Param("endDateTime") LocalDateTime endDateTime,
	    Pageable pageable
	);

    // 카테고리 별 게시글 목록 조회
    @Query("SELECT b FROM Board b WHERE b.boardCategory.boardCategoryNo = :categoryNo ORDER BY b.boardNo ASC")
    Page<Board> findBoardsByCategoryNo(@Param("categoryNo") Long categoryNo, Pageable pageable);

    // 카테고리에서 게시글 total 조회
    @Query("SELECT COUNT(b) FROM Board b WHERE b.boardCategory.boardCategoryNo = :categoryNo")
    int countByCategoryNo(@Param("categoryNo") Long categoryNo);

	// 게시물 상세보기
    @EntityGraph(attributePaths = {"boardCategory", "user"})
    Board findByBoardNo(Long boardNo);

    /* 대시보드 */
    @Query("SELECT COUNT(b) FROM Board b WHERE b.isDeleted = false")
    long countTotalBoards();

    @Query("SELECT COUNT(b) FROM Board b WHERE DATE(b.createdAt) = CURRENT_DATE")
	long countDailyNewBoards();

    @Query("SELECT COUNT(b) FROM Board b WHERE b.isDeleted = true AND DATE(b.deletedAt) = CURRENT_DATE")
	long countDailyDeletedBoards();

    @Query("SELECT b FROM Board b JOIN FETCH b.boardCategory")
    List<Board> findAllWithCategory();
    
    @Query("SELECT COUNT(b) FROM Board b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    Long countBoardsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Board b WHERE b.isDeleted = true ORDER BY b.deletedAt DESC")
    List<Board> findRecentDeletedBoards(Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.isDeleted = true AND DATE(b.deletedAt) = CURRENT_DATE ORDER BY b.deletedAt DESC")
    List<Board> findTodayDeletedBoards(Pageable pageable);
	
    @Modifying
    @Transactional
    @Query("UPDATE Board b SET b.boardViewCnt = b.boardViewCnt + :count WHERE b.boardNo = :boardNo")
    void updateViewCount(@Param("boardNo") Long boardNo, @Param("count") int count);

    // 게시글 통합겸색
    @Query("""
        SELECT b FROM Board b
        WHERE b.boardCategory.boardCategoryNo = :categoryNo
        AND b.isDeleted = false
        AND (:keyword IS NULL OR :keyword = '' OR
             b.boardTitle LIKE %:keyword% OR
             b.boardContent LIKE %:keyword% OR
             b.user.userNickname LIKE %:keyword%)
        ORDER BY b.boardNo ASC
    """)
    Page<Board> searchBoardsByCategory(
            @Param("categoryNo") Long categoryNo,
            @Param("keyword") String keyword,
            Pageable pageable
    );



}
