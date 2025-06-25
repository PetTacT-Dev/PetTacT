package com.pettact.api.reply.repository;

import com.pettact.api.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("SELECT r FROM Reply r WHERE r.board.boardNo = :boardNo")
    List<Reply> findRepliesByBoardNo(@Param("boardNo") Long boardNo);

    boolean existsByParentReply_ReplyNo(Long replyNo);
}
