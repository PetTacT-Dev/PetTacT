package com.pettact.api.Category.controller;

import com.pettact.api.Category.service.CategoryService;
import com.pettact.api.Category.dto.CreateDto;
import com.pettact.api.Category.dto.ResponseDto;
import com.pettact.api.board.dto.BoardResponseDto;
import com.pettact.api.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/board-categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BoardService boardService;

    /*
     * POST /v1/board-categories
     * 게시판 카테고리 생성
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseDto> createCategory(@RequestBody CreateDto createDto) {
        ResponseDto responseDto = categoryService.createCategory(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /*
     * GET /v1/board-categories
     * 게시판 카테고리 목록 조회
     */

    @GetMapping
    public ResponseEntity<List<ResponseDto>> getAllCategory() {
        List<ResponseDto> categories = categoryService.getAllCategory();
        return ResponseEntity.ok(categories);
    }

    /*
     * GET v1/board/{categoryNo}/boards
     * 카테고리 별 리스트 조회
     * - 댓글 수 조회수
     */

    @GetMapping("/{categoryNo}/boards")
    public ResponseEntity<Page<BoardResponseDto>> getBoardsByCategory(
            @PathVariable("categoryNo") Long categoryNo,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchKeyword", required = false) String searchKeyword) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BoardResponseDto> result = boardService.findBoardsByCategory(categoryNo, pageable, searchKeyword);

        return ResponseEntity.ok(result);
    }


    /*
     * GET v1/board-categories/{boardCategoryNo}
     * 특정 게시판 카테고리 상세 조회 (단건)
     */

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{boardCategoryNo}")
    public ResponseEntity<ResponseDto> getCategoryByNo(@PathVariable("boardCategoryNo") Long boardCategoryNo) {
        ResponseDto responseDto = categoryService.getCategoryByNo(boardCategoryNo);
        return ResponseEntity.ok(responseDto);
    }

    /*
     * PATCH v1/board-categories/{boardCategoryNo}
     * 게시판 카테고리 정보 수정
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{boardCategoryNo}")
    public ResponseEntity<ResponseDto> updatedCategory(@PathVariable Long boardCategoryNo,
                                                       @RequestBody CreateDto createDto) {
        ResponseDto responseDto = categoryService.updateCategory(boardCategoryNo, createDto);
        return ResponseEntity.ok(responseDto);
    }

    /*
     * DELETE v1/board-categories/{boardCategoryNo}
     * 게시판 카테고리 삭제
     */

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{boardCategoryNo}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long boardCategoryNo) {
       categoryService.deleteCategory(boardCategoryNo);
       return ResponseEntity.accepted().build();
    }

}
