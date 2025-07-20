package com.pettact.api.recommend.replyRecommend.service;


import com.pettact.api.recommend.boardRecommend.entity.BoardRecommend;
import com.pettact.api.recommend.replyRecommend.dto.ReplyRecommendDto;
import com.pettact.api.recommend.replyRecommend.entity.ReplyRecommend;
import com.pettact.api.recommend.replyRecommend.repository.ReplyRecommendRepository;
import com.pettact.api.reply.entity.Reply;
import com.pettact.api.reply.repository.ReplyRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ReplyRecommendService {

    @Autowired
    private ReplyRecommendRepository replyRecommendRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createRecommend(Long replyNo, Long userNo) {
        log.info("🔍 댓글 추천 요청 - replyNo: {}, userNo: {}", replyNo, userNo);

        // 중복 추천 확인
        if (replyRecommendRepository.existsByReply_ReplyNoAndUsers_UserNo(replyNo, userNo)) {
            log.warn("❌ 이미 추천한 댓글입니다 - replyNo: {}, userNo: {}", replyNo, userNo);
            throw new IllegalArgumentException("이미 추천한 댓글입니다.");
        }

        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        log.info("✅ 댓글 조회 성공 - replyNo: {}", reply.getReplyNo());

        Users users = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        log.info("✅ 사용자 조회 성공 - userNo: {}", users.getUserNo());

        ReplyRecommend replyRecommend = new ReplyRecommend(
                reply,
                users,
                LocalDateTime.now()
        );

        ReplyRecommend saved = replyRecommendRepository.save(replyRecommend);
        log.info("✅ 댓글 추천 저장 완료 - recommendId: {}", saved.getReplyRecommendNo());

        // 현재 총 추천 수 확인
        int totalCount = replyRecommendRepository.countByReply_ReplyNo(replyNo);
        log.info("📊 현재 댓글 총 추천 수: {}", totalCount);
    }

    @Transactional
    public void cancelRecommend(Long replyNo, Long userNo) {
        log.info("🔍 댓글 추천 취소 요청 - replyNo: {}, userNo: {}", replyNo, userNo);

        ReplyRecommend replyRecommend = replyRecommendRepository
                .findByReplyAndUser(replyNo, userNo)
                .orElseThrow(() -> new IllegalArgumentException("추천하지 않은 댓글입니다."));

        log.info("✅ 댓글 추천 기록 찾기 성공 - recommendId: {}", replyRecommend.getReplyRecommendNo());

        replyRecommendRepository.delete(replyRecommend);
        log.info("✅ 댓글 추천 취소 완료");

        // 현재 총 추천 수 확인
        int totalCount = replyRecommendRepository.countByReply_ReplyNo(replyNo);
        log.info("📊 현재 댓글 총 추천 수: {}", totalCount);
    }

    // ✅ 게시글처럼 추천 상태 확인 메서드 추가
    public boolean isUserRecommended(Long replyNo, Long userNo) {
        if (userNo == null) return false;
        log.info("🔍 댓글 추천 상태 확인 - replyNo: {}, userNo: {}", replyNo, userNo);

        boolean result = replyRecommendRepository.existsByReply_ReplyNoAndUsers_UserNo(replyNo, userNo);
        log.info("📤 댓글 추천 상태 결과: {}", result);

        return result;
    }
}