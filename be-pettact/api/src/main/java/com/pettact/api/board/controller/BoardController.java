package com.pettact.api.board.controller;

import com.pettact.api.board.service.BoardService;
import com.pettact.api.board.dto.BoardCreateDto;
import com.pettact.api.board.dto.BoardResponseDto;
import com.pettact.api.file.dto.FileDto;
import com.pettact.api.file.entity.File;
import com.pettact.api.file.service.MultiFileService;
import com.pettact.api.product.dto.ProductDTO;
import com.pettact.api.security.vo.CustomUserDetails;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// 구분할지 말지

@RestController
@RequestMapping("/v1/board")
public class BoardController {

    @Autowired
    private BoardService boardService;
    @Autowired
    private MultiFileService multiFileService;

    /*
     * POST /v1/board
     * 게시글 생성
     */

    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @RequestPart("data") BoardCreateDto boardCreateDto,
            @RequestPart(value = "files", required = false) MultipartFile[] files, // 파일 추가
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userNo = userDetails.getUserEntity().getUserNo();

        BoardResponseDto boardResponse = boardService.createBoard(boardCreateDto, userNo);

        if (files != null && files.length > 0) {
            List<FileDto> uploadedFiles = multiFileService.createFiles(
                    File.ReferenceTable.BOARD,
                    boardResponse.getBoardNo(),
                    files,
                    userNo
            );
            boardResponse.setFiles(uploadedFiles);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponse);
    }

    @GetMapping("/main/popular-community")
    public List<BoardResponseDto> getPopularBoardsForMain() {
        List<BoardResponseDto> allBoards = boardService.getAllBoard();

        return allBoards.stream()
                .filter(dto -> dto.getBoardRecommendCount() >= 1 || dto.getTotalReplyCount() >= 1)
                .sorted(Comparator.comparing(BoardResponseDto::getTotalReplyCount).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    /*
     * GET /v1/board
     * 게시글 목록 조회
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
    public ResponseEntity<BoardResponseDto> getBoardByNo(@PathVariable("boardNo") Long boardNo,  HttpSession session) {
    	String sessionId = session.getId();
        BoardResponseDto board = boardService.getBoardByNo(boardNo, sessionId);
        List<FileDto> files = multiFileService.getFilesByReference(File.ReferenceTable.BOARD,
                boardNo);
        board.setFiles(files);
        return ResponseEntity.ok(board);
    }

    /*
     * PUT v1/board/{boardNo}
     * 게시글 정보 수정
     */

    @PutMapping("/{boardNo}")
    public ResponseEntity<BoardResponseDto> updateBoard(
            @PathVariable Long boardNo,
            @RequestPart("data") BoardCreateDto boardCreateDto,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "deletedFileIds", required = false) List<Long> deletedFileIds,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userNo = userDetails.getUserEntity().getUserNo();

        BoardResponseDto boardResponseDto = boardService.updateBoard(
                boardNo, boardCreateDto, userNo, files, deletedFileIds
        );

        return ResponseEntity.ok(boardResponseDto);
    }


    /*
     * DELETE v1/board/{boardNo}
     * 게시글 삭제
     */

    @DeleteMapping("/{boardNo}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardNo,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUserEntity().getUserNo();
        boardService.deleteBoard(boardNo, userNo);
        return ResponseEntity.noContent().build();
    }
    
    // ------------------ 인기 게시글 TOP 10 ------------------
    @GetMapping("/popular")
    public List<BoardResponseDto> getPopularBoards(
        @RequestParam(value = "categoryNo", required = false) Long categoryNo,
        @RequestParam(value = "count", defaultValue = "10") int count
    ) {
        return boardService.getPopularBoards(categoryNo, count);
    }
}