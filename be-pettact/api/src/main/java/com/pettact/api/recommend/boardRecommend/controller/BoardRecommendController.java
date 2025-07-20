package com.pettact.api.recommend.boardRecommend.controller;

import com.pettact.api.recommend.boardRecommend.dto.BoardRecommendDto;
import com.pettact.api.recommend.boardRecommend.service.BoardRecommendService;
import com.pettact.api.security.vo.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/board/{boardNo}/recommend")
public class BoardRecommendController {

    @Autowired
    private BoardRecommendService boardRecommendService;

    @PostMapping
    public ResponseEntity<Void> createBoardRecommend(@PathVariable("boardNo") Long boardNo,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUserEntity().getUserNo();
        boardRecommendService.createRecommend(boardNo, userNo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBoardRecommend(@PathVariable("boardNo") Long boardNo,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUserEntity().getUserNo();
        boardRecommendService.cancelRecommend(boardNo, userNo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<Boolean> checkRecommendStatus(
            @PathVariable("boardNo") Long boardNo,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.ok(false);
        }

        Long userNo = userDetails.getUserEntity().getUserNo();
        boolean isRecommended = boardRecommendService.isUserRecommended(boardNo, userNo);
        return ResponseEntity.ok(isRecommended);
    }
}
