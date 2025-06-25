package com.pettact.api.reply.service;


import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.reply.dto.ReplyRequestDto;
import com.pettact.api.reply.dto.ReplyResponseDto;
import com.pettact.api.reply.entity.Reply;
import com.pettact.api.reply.repository.ReplyRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReplyService {
    
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ReplyResponseDto createReply(Long boardNo, ReplyRequestDto replyRequestDto) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(()-> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Users users = userRepository.findById(replyRequestDto.getUserNo())
                .orElseThrow(()-> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Reply parentReply = null;
        if (replyRequestDto.getParentReplyNo() != null) {
            parentReply = replyRepository.findById(replyRequestDto.getParentReplyNo())
                    .orElseThrow(()-> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }
        Reply reply = replyRequestDto.toEntity(board, parentReply, users);
        Reply savedReply = replyRepository.save(reply);
        return ReplyResponseDto.fromEntity(savedReply);
    }


    public List<ReplyResponseDto> getAllReplies(Long boardNo) {
        boardRepository.findById(boardNo)
                .orElseThrow(()-> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        List<Reply> replies = replyRepository.findRepliesByBoardNo(boardNo);
        return buildHierarchy(replies);
    }

    /**
     * 댓글 리스트를 계층구조로 변환
     */
    private List<ReplyResponseDto> buildHierarchy(List<Reply> replies) {
        Map<Long, ReplyResponseDto> replyMap = new HashMap<>();
        List<ReplyResponseDto> rootReplies = new ArrayList<>();

        // 모든 댓글을 DTO로 변환하여 Map에 저장
        for (Reply reply : replies) {
            ReplyResponseDto dto = ReplyResponseDto.fromEntity(reply);
            replyMap.put(reply.getReplyNo(), dto);
        }

        // 계층구조 구성
        for (Reply reply : replies) {
            ReplyResponseDto dto = replyMap.get(reply.getReplyNo());

            if (reply.getParentReply() == null) {
                // 최상위 댓글 (부모가 없는 댓글)
                rootReplies.add(dto);
            } else {
                // 대댓글 (부모 댓글의 childReplies에 추가)
                Long parentId = reply.getParentReply().getReplyNo();
                ReplyResponseDto parent = replyMap.get(parentId);
                if (parent != null) {
                    parent.getChildReplies().add(dto);
                }
            }
        }

        return rootReplies;
    }

    @Transactional
    public ReplyResponseDto updateReply(Long replyNo, ReplyRequestDto replyRequestDto) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!reply.getUser().getUserNo().equals(replyRequestDto.getUserNo())) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다.");
        }

        reply.updateContent(replyRequestDto);

        Reply updatedReply = replyRepository.save(reply);
        return ReplyResponseDto.fromEntity(updatedReply);

    }


    public void deleteReply(Long replyNo) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(()-> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        boolean hasChildReplies = replyRepository.existsByParentReply_ReplyNo(replyNo);

        if (hasChildReplies) {
            // 대댓글이 있으면 구조 유지(soft delete)
            reply.markAsDeleted();
        } else {
            // 대댓글 x -> 완전 삭제
            replyRepository.delete(reply);
        }


    }


}
