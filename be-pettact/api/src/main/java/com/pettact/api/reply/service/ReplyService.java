package com.pettact.api.reply.service;


import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.notification.dto.NotificationReqDTO;
import com.pettact.api.notification.enums.NotificationType;
import com.pettact.api.notification.enums.TargetType;
import com.pettact.api.notification.service.NotificationService;
import com.pettact.api.recommend.replyRecommend.repository.ReplyRecommendRepository;
import com.pettact.api.reply.dto.ReplyRequestDto;
import com.pettact.api.reply.dto.ReplyResponseDto;
import com.pettact.api.reply.entity.Reply;
import com.pettact.api.reply.repository.ReplyRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReplyService {

    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReplyRecommendRepository replyRecommendRepository;
    @Autowired
    private NotificationService notificationService;

    @Transactional
    public ReplyResponseDto createReply(Long boardNo, ReplyRequestDto replyRequestDto, Long userNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(()-> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Users users = userRepository.findById(userNo)
                .orElseThrow(()-> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Reply parentReply = null;
        if (replyRequestDto.getParentReplyNo() != null) {
            parentReply = replyRepository.findById(replyRequestDto.getParentReplyNo())
                    .orElseThrow(()-> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }
        // DTO의 userNo를 인증된 사용자로 덮어쓰기!
        replyRequestDto.setUserNo(userNo);

        Reply reply = replyRequestDto.toEntity(board, parentReply, users);
        Reply savedReply = replyRepository.save(reply);

        sendCommentNotification(board, parentReply, users, reply);

        return ReplyResponseDto.fromEntity(savedReply);
    }

    private void sendCommentNotification(Board board, Reply parentReply, Users commenter, Reply reply) {
        Long userNo = commenter.getUserNo();

        if (parentReply != null) {
            Long parentUserNo = parentReply.getUser().getUserNo();
            Long boardOwnerNo = board.getUser().getUserNo();

            if (!parentUserNo.equals(userNo)) {
                NotificationReqDTO dto = NotificationReqDTO.of(
                        userNo, parentUserNo,
                        NotificationType.COMMENT,
                        board.getBoardNo(), TargetType.BOARD,
                        "내 댓글에 대댓글이 달렸습니다.",
                        commenter.getUserNickname() + "님이 대댓글을 남겼습니다: " + reply.getContent()
                );
                notificationService.sendNotification(dto);
            }

            if (!boardOwnerNo.equals(parentUserNo) && !boardOwnerNo.equals(userNo)) {
                NotificationReqDTO dto = NotificationReqDTO.of(
                        userNo, boardOwnerNo,
                        NotificationType.COMMENT,
                        board.getBoardNo(), TargetType.BOARD,
                        "새 댓글이 달렸습니다.",
                        commenter.getUserNickname() + "님이 댓글을 남겼습니다: " + reply.getContent()
                );
                notificationService.sendNotification(dto);
            }

        } else {
            Long boardOwnerNo = board.getUser().getUserNo();
            if (!boardOwnerNo.equals(userNo)) {
                NotificationReqDTO dto = NotificationReqDTO.of(
                        userNo, boardOwnerNo,
                        NotificationType.COMMENT,
                        board.getBoardNo(), TargetType.BOARD,
                        "새 댓글이 달렸습니다.",
                        commenter.getUserNickname() + "님이 댓글을 남겼습니다: " + reply.getContent()
                );
                notificationService.sendNotification(dto);
            }
        }
    }


//    public Page<ReplyResponseDto> getAllReplies(Long boardNo, Pageable pageable) {
//        boardRepository.findById(boardNo)
//                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
//
//
//        List<Reply> replies = replyRepository.findRepliesByBoardNo(boardNo);
//        List<ReplyResponseDto> hierarchyReplies = buildHierarchy(replies);
//
//        int start = (int) pageable.getOffset();
//        int end = Math.min((start + pageable.getPageSize()), hierarchyReplies.size());
//        List<ReplyResponseDto> pageContent = hierarchyReplies.subList(start, end);
//
//        return new PageImpl<>(pageContent, pageable, hierarchyReplies.size());
//    }

    public Page<ReplyResponseDto> getAllReplies(Long boardNo, Pageable pageable) {
        boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));


        // ✅ 모든 방법으로 테스트
        System.out.println("=== boardNo: " + boardNo + " ===");

        // 1. 기존 JPQL 쿼리
        List<Reply> replies1 = replyRepository.findRepliesByBoardNo(boardNo);
        System.out.println("JPQL 쿼리 결과: " + replies1.size());

        // 2. 네이티브 쿼리
        List<Reply> replies2 = replyRepository.findRepliesByBoardNoNative(boardNo);
        System.out.println("네이티브 쿼리 결과: " + replies2.size());

        // 3. 메서드 쿼리
        List<Reply> replies3 = replyRepository.findByBoard_BoardNoOrderByCreatedAt(boardNo);
        System.out.println("메서드 쿼리 결과: " + replies3.size());

        // 4. 전체 조회 후 필터링
        List<Reply> allReplies = replyRepository.findAll();
        System.out.println("전체 댓글 개수: " + allReplies.size());

        // 어떤 방법이 작동하는지 확인하고 그걸 사용
        List<Reply> replies = replies2.isEmpty() ? replies3 : replies2;

        // ✅ buildHierarchy 전후 확인
        List<ReplyResponseDto> hierarchyReplies = buildHierarchy(replies);
        System.out.println("=== buildHierarchy 후 개수: " + hierarchyReplies.size() + " ===");

        // ✅ 페이징 정보 확인
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), hierarchyReplies.size());
        System.out.println("=== 페이징 정보: start=" + start + ", end=" + end + ", pageSize=" + pageable.getPageSize() + " ===");

        List<ReplyResponseDto> pageContent = hierarchyReplies.subList(start, end);
        System.out.println("=== 최종 반환 개수: " + pageContent.size() + " ===");

        return new PageImpl<>(pageContent, pageable, hierarchyReplies.size());
    }


    /**
     * 댓글 리스트를 계층구조로 변환
     */
    private List<ReplyResponseDto> buildHierarchy(List<Reply> replies) {
        // 1. 모든 댓글을 DTO로 변환하여 Map에 저장
        Map<Long, ReplyResponseDto> replyMap = new HashMap<>();
        for (Reply reply : replies) {
            ReplyResponseDto dto = ReplyResponseDto.fromEntity(reply);

            // 댓글 추천수 설정
            int recommendCount = replyRecommendRepository.countByReplyNo(reply.getReplyNo());
            dto.setRecommendCount(recommendCount);

            replyMap.put(reply.getReplyNo(), dto);
        }

        // 2. 부모-자식 관계 설정 및 depth 계산
        for (Reply reply : replies) {
            ReplyResponseDto dto = replyMap.get(reply.getReplyNo());

            if (reply.getParentReply() == null) {
                // 최상위 댓글
                dto.setDepth(0);
            } else {
                // 대댓글 - 부모의 depth + 1
                ReplyResponseDto parentDto = replyMap.get(reply.getParentReply().getReplyNo());
                if (parentDto != null) {
                    dto.setDepth(parentDto.getDepth() + 1);
                }
            }
        }

        // 3. 계층 구조로 정렬
        return buildHierarchicalList(replyMap.values().stream()
                .filter(dto -> dto.getDepth() == 0)
                .sorted(Comparator.comparing(ReplyResponseDto::getCreatedAt))
                .collect(Collectors.toList()), replyMap);
    }

    // 계층 구조 유지하면서 리스트 구성
    private List<ReplyResponseDto> buildHierarchicalList(List<ReplyResponseDto> parentReplies, Map<Long, ReplyResponseDto> allReplies) {
        List<ReplyResponseDto> result = new ArrayList<>();

        for (ReplyResponseDto parent : parentReplies) {
            // 1. 부모 댓글 추가
            result.add(parent);

            // 2. 해당 부모의 자식 댓글들 찾기
            List<ReplyResponseDto> children = allReplies.values().stream()
                    .filter(dto -> dto.getParentReplyNo() != null &&
                            dto.getParentReplyNo().equals(parent.getReplyNo()))
                    .sorted(Comparator.comparing(ReplyResponseDto::getCreatedAt))
                    .collect(Collectors.toList());

            // 3. 자식 댓글들도 재귀적으로 처리
            result.addAll(buildHierarchicalList(children, allReplies));
        }

        return result;
    }

    @Transactional
    public ReplyResponseDto updateReply(Long replyNo, ReplyRequestDto replyRequestDto, Long userNo) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!reply.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다.");
        }
        reply.updateContent(replyRequestDto);
        Reply updatedReply = replyRepository.save(reply);
        return ReplyResponseDto.fromEntity(updatedReply);
    }


    public void deleteReply(Long replyNo, Long userNo) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(()-> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!reply.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
        }
        boolean hasChildReplies = replyRepository.existsByParentReply_ReplyNo(replyNo);

        if (hasChildReplies) {
            // 대댓글이 있으면 구조 유지(soft delete)
            reply.markAsDeleted();
        } else {
            // 대댓글 x -> 완전 삭제
            replyRepository.delete(reply);
        }
    }


    public List<ReplyResponseDto> getPopularReplies(Long boardNo, int limit) {
        Pageable pageable = PageRequest.of(0, limit * 2);
        List<Reply> popularReplies = replyRepository.findPopularRepliesByBoardNo(boardNo, pageable);

        return popularReplies.stream()
                .map(reply -> {
                    ReplyResponseDto dto = ReplyResponseDto.fromEntity(reply);
                    int recommendCount = replyRecommendRepository.countByReplyNo(reply.getReplyNo());
                    dto.setRecommendCount(recommendCount);
                    dto.setDepth(0);
                    return dto;
                })
                .filter(dto -> dto.getRecommendCount() >= 1)
                .limit(limit) // 실제 인기 댓글 수만큼 자르기
                .collect(Collectors.toList());
    }

    public int countByBoardNo(Long boardNo) {
        return replyRepository.countByBoardNo(boardNo);
    }
}