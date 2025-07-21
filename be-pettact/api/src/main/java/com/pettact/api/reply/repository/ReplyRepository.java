package com.pettact.api.reply.repository;

import com.pettact.api.reply.entity.Reply;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

//    @Query("SELECT r FROM Reply r WHERE r.board.boardNo = :boardNo")
//    List<Reply> findRepliesByBoardNo(@Param("boardNo") Long boardNo);

    // ✅ 기존 JPQL 쿼리
    @Query("SELECT r FROM Reply r WHERE r.board.boardNo = :boardNo")
    List<Reply> findRepliesByBoardNo(@Param("boardNo") Long boardNo);

    // ✅ 네이티브 쿼리로 테스트
    @Query(value = "SELECT * FROM reply WHERE board_no = :boardNo ORDER BY created_at", nativeQuery = true)
    List<Reply> findRepliesByBoardNoNative(@Param("boardNo") Long boardNo);

    // ✅ Spring Data JPA 메서드 쿼리
    List<Reply> findByBoard_BoardNoOrderByCreatedAt(Long boardNo);

    boolean existsByParentReply_ReplyNo(Long replyNo);

    @Query("SELECT r FROM Reply r " +
            "WHERE r.board.boardNo = :boardNo " +
            "AND r.parentReply IS NULL " +  // 최상위 댓글만
            "ORDER BY (SELECT COUNT(rr) FROM ReplyRecommend rr WHERE rr.reply.replyNo = r.replyNo) DESC")
    List<Reply> findPopularRepliesByBoardNo(@Param("boardNo") Long boardNo, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reply r WHERE r.board.boardNo = :boardNo")
    int countByBoardNo(@Param("boardNo") Long boardNo);
}
