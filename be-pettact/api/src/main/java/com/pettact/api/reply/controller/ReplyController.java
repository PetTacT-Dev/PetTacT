package com.pettact.api.reply.controller;

import com.pettact.api.reply.dto.ReplyRequestDto;
import com.pettact.api.reply.dto.ReplyResponseDto;
import com.pettact.api.reply.service.ReplyService;
import com.pettact.api.security.vo.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    /*
     * POST /v1/board/{boardNo}/replies
     * 댓글 생성
     */

    @PostMapping("/board/{boardNo}/replies")
    public ResponseEntity<ReplyResponseDto> createReply(@PathVariable("boardNo") Long boardNo, @RequestBody ReplyRequestDto replyRequestDto,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUserEntity().getUserNo();
        ReplyResponseDto createdDto = replyService.createReply(boardNo, replyRequestDto, userNo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);

    }

    /*
     * GET /v1/board/{boardNo}/replies
     * {boardNo}에 대한 댓글 조회
     */

    @GetMapping("/board/{boardNo}/replies")
    public ResponseEntity<Page<ReplyResponseDto>> getAllReplies
    (@PathVariable("boardNo") Long boardNo,
     @RequestParam(name = "page", defaultValue = "0") int page,
     @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReplyResponseDto> responseDtos = replyService.getAllReplies(boardNo, pageable);
        return ResponseEntity.ok(responseDtos);
    }

    /*
     * GET /v1/board/{boardNo}/popular
     * 인기 댓글 조회
     */
    @GetMapping("/board/{boardNo}/replies/popular")
    public ResponseEntity<List<ReplyResponseDto>> getPopularReplies(@PathVariable("boardNo") Long boardNo,
                                                                    @RequestParam(defaultValue = "3") int limit) {
        List<ReplyResponseDto> popularReplies = replyService.getPopularReplies(boardNo, limit);
        return ResponseEntity.ok(popularReplies);

    }

    /*
     * PATCH /v1/replies/{replyNo}
     * 댓글 수정
     */

    @PatchMapping("/replies/{replyNo}")
    public ResponseEntity<ReplyResponseDto> updateReply(@PathVariable Long replyNo, @RequestBody ReplyRequestDto replyRequestDto,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUserEntity().getUserNo();
        ReplyResponseDto updatedDto = replyService.updateReply(replyNo, replyRequestDto, userNo);
        return ResponseEntity.ok(updatedDto);
    }

    /*
     * DELETE /v1/replies/{replyNo}
     * 댓글 삭제
     */

    @DeleteMapping("/replies/{replyNo}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyNo,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userNo = userDetails.getUserEntity().getUserNo();
        replyService.deleteReply(replyNo, userNo);
        return ResponseEntity.noContent().build();
    }
}
