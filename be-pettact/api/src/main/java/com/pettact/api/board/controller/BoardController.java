package com.pettact.api.board.controller;

import com.pettact.api.board.service.BoardService;
import com.pettact.api.board.dto.BoardCreateDto;
import com.pettact.api.board.dto.BoardResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 구분할지 말지

@RestController
@RequestMapping("/v1/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    /*
     * POST /v1/board
     * 게시글 생성
     */

    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardCreateDto boardCreateDto) {
        BoardResponseDto boardResponseDto = boardService.createBoard(boardCreateDto);
        return ResponseEntity.ok(boardResponseDto);
    }

    /*
     * GET /v1/board
     * 게시긒 목록 조회
     */

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoard() {
        List<BoardResponseDto> boards = boardService.getAllBoard();
        return ResponseEntity.ok(boards);
    }

    /*
     * GET v1/board/{boardNo}
     * 게시글 정보 조회(단건)
     */

    @GetMapping("/{boardNo}")
    public ResponseEntity<BoardResponseDto> getBoardByNo(@PathVariable Long boardNo) {
        BoardResponseDto board = boardService.getBoardByNo(boardNo);
        return ResponseEntity.ok(board);
    }

    /*
     * PUT v1/board/{boardNo}
     * 게시글 정보 수정
     */

    @PutMapping("/{boardNo}")
    public ResponseEntity<BoardResponseDto> updateBoard
            (@PathVariable Long boardNo, @RequestBody BoardCreateDto boardCreateDto) {
        BoardResponseDto boardResponseDto = boardService.updateBoard(boardNo, boardCreateDto);
        return ResponseEntity.ok(boardResponseDto);
    }

    /*
     * DELETE v1/board/{boardNo}
     * 게시글 삭제
     */

    @DeleteMapping("/{boardNo}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardNo) {
        boardService.deleteBoard(boardNo);
        return ResponseEntity.noContent().build();
    }
}