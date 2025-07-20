package com.pettact.api.recommend.replyRecommend.repository;

import com.pettact.api.recommend.replyRecommend.entity.ReplyRecommend;
import com.pettact.api.reply.entity.Reply;
import com.pettact.api.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReplyRecommendRepository extends JpaRepository<ReplyRecommend, Long> {


    @Query("SELECT rr FROM ReplyRecommend rr WHERE rr.reply.replyNo = :replyNo AND rr.users.userNo = :userNo")
    Optional<ReplyRecommend> findByReplyAndUser(@Param("replyNo") Long replyNo, @Param("userNo") Long userNo);

    boolean existsByReply_ReplyNoAndUsers_UserNo(Long replyNo, Long userNo);

    @Query("SELECT COUNT(rr) FROM ReplyRecommend rr WHERE rr.reply.replyNo = :replyNo")
    int countByReplyNo(@Param("replyNo") Long replyNo);

    int countByReply_ReplyNo(Long replyNo);

    Optional<Object> findByReply_ReplyNoAndUsers_UserNo(Long replyNo, Long userNo);
}
