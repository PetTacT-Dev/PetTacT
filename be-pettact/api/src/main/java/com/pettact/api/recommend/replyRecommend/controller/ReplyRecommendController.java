package com.pettact.api.recommend.replyRecommend.controller;

import com.pettact.api.recommend.replyRecommend.dto.ReplyRecommendDto;
import com.pettact.api.recommend.replyRecommend.entity.ReplyRecommend;
import com.pettact.api.recommend.replyRecommend.service.ReplyRecommendService;
import com.pettact.api.security.vo.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/replies/{replyNo}/recommend")
public class ReplyRecommendController {

    @Autowired
    private ReplyRecommendService replyRecommendService;

    @PostMapping
    public ResponseEntity<Void> createReplyRecommend(@PathVariable("replyNo") Long replyNo,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUserEntity().getUserNo();
        replyRecommendService.createRecommend(replyNo, userNo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelReplyRecommend(@PathVariable("replyNo") Long replyNo,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUserEntity().getUserNo();
        replyRecommendService.cancelRecommend(replyNo, userNo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // ✅ 게시글처럼 GET 메서드 추가
    @GetMapping
    public ResponseEntity<Boolean> checkReplyRecommendStatus(
            @PathVariable("replyNo") Long replyNo,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.ok(false);
        }

        Long userNo = userDetails.getUserEntity().getUserNo();
        boolean isRecommended = replyRecommendService.isUserRecommended(replyNo, userNo);
        return ResponseEntity.ok(isRecommended);
    }
}