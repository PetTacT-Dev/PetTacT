package com.pettact.api.recommend.boardRecommend.service;

import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.recommend.boardRecommend.dto.BoardRecommendDto;
import com.pettact.api.recommend.boardRecommend.entity.BoardRecommend;
import com.pettact.api.recommend.boardRecommend.repository.BoardRecommendRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class BoardRecommendService {

    @Autowired
    private BoardRecommendRepository boardRecommendRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createRecommend(Long boardNo, Long userNo) {
        log.info("🔍 추천 요청 - boardNo: {}, userNo: {}", boardNo, userNo);

        // 중복 추천 확인 (ID로 확인)
        if (boardRecommendRepository.existsByBoardAndUser(boardNo, userNo)) {
            log.warn("❌ 이미 추천한 게시글입니다 - boardNo: {}, userNo: {}", boardNo, userNo);
            throw new IllegalArgumentException("이미 추천한 게시글입니다.");
        }

        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        log.info("✅ 게시글 조회 성공 - boardNo: {}", board.getBoardNo());

        Users users = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다"));
        log.info("✅ 사용자 조회 성공 - userNo: {}", users.getUserNo());

        BoardRecommend boardRecommend = new BoardRecommend(
                board,
                users,
                LocalDateTime.now()
        );

        BoardRecommend saved = boardRecommendRepository.save(boardRecommend);
        log.info("✅ 추천 저장 완료 - recommendId: {}", saved.getBoardRecommendNo());

        // 현재 총 추천 수 확인
        int totalCount = boardRecommendRepository.countByBoardNo(boardNo);
        log.info("📊 현재 게시글 총 추천 수: {}", totalCount);
    }

    @Transactional
    public void cancelRecommend(Long boardNo, Long userNo) {
        log.info("🔍 추천 취소 요청 - boardNo: {}, userNo: {}", boardNo, userNo);

        BoardRecommend boardRecommend = boardRecommendRepository
                .findByBoardAndUser(boardNo, userNo)  // ✅ Repository에 있는 메서드명 사용
                .orElseThrow(() -> new IllegalArgumentException("추천하지 않은 게시글입니다."));

        boardRecommendRepository.delete(boardRecommend);
        log.info("✅ 추천 취소 완료 - boardNo: {}, userNo: {}", boardNo, userNo);

        // 현재 총 추천 수 확인
        int totalCount = boardRecommendRepository.countByBoardNo(boardNo);
        log.info("📊 현재 게시글 총 추천 수: {}", totalCount);
    }

    public boolean isUserRecommended(Long boardNo, Long userNo) {
        if (userNo == null) return false;
        return boardRecommendRepository.existsByBoardAndUser(boardNo, userNo);  // ✅ Repository에 있는 메서드명 사용
    }

}
