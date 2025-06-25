package com.pettact.api.reply.controller;

import com.pettact.api.reply.dto.ReplyRequestDto;
import com.pettact.api.reply.dto.ReplyResponseDto;
import com.pettact.api.reply.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ReplyResponseDto> createReply(@PathVariable Long boardNo,
                                                        @RequestBody ReplyRequestDto replyRequestDto) {
        ReplyResponseDto createdDto = replyService.createReply(boardNo, replyRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);

    }

    /*
     * GET /v1/board/{boardNo}/replies
     * {boardNo}에 대한 댓글 조회
     */

    @GetMapping("/board/{boardNo}/replies")
    public ResponseEntity<List<ReplyResponseDto>> getAllReplies(@PathVariable Long boardNo) {
        List<ReplyResponseDto> responseDtos = replyService.getAllReplies(boardNo);
        return ResponseEntity.ok(responseDtos);
    }

    /*
     * PATCH /v1/replies/{replyNo}
     * 댓글 수정
     */

    @PatchMapping("/replies/{replyNo}")
    public ResponseEntity<ReplyResponseDto> updateReply(@PathVariable Long replyNo,
                                                       @RequestBody ReplyRequestDto replyRequestDto) {
        ReplyResponseDto updatedDto = replyService.updateReply(replyNo, replyRequestDto);
        return ResponseEntity.ok(updatedDto);
    }

    /*
     * DELETE /v1/replies/{replyNo}
     * 댓글 삭제
     */

    @DeleteMapping("/replies/{replyNo}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyNo) {
        replyService.deleteReply(replyNo);
        return ResponseEntity.noContent().build();
    }
}
