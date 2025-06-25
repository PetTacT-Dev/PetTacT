package com.pettact.api.Category.controller;

import com.pettact.api.Category.service.CategoryService;
import com.pettact.api.Category.dto.CreateDto;
import com.pettact.api.Category.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/board-categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /*
     * POST /v1/board-categories
     * 게시판 카테고리 생성
     */

    @PostMapping
    public ResponseEntity<ResponseDto> createCategory(@RequestBody CreateDto createDto) {
        ResponseDto responseDto = categoryService.createCategory(createDto);
        // return ResponseEntity.ok(responseDto);
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
     * GET v1/board-categories/{boardCategoryNo}
     * 특정 게시판 카테고리 상세 조회 (단건)
     */

    @GetMapping("/{boardCategoryNo}")
    public ResponseEntity<ResponseDto> getCategoryByNo(@PathVariable Long boardCategoryNo) {
        ResponseDto responseDto = categoryService.getCategoryByNo(boardCategoryNo);
        return ResponseEntity.ok(responseDto);
    }

    /*
     * PATCH v1/board-categories/{boardCategoryNo}
     * 게시판 카테고리 정보 수정
     */

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

    @DeleteMapping("/{boardCategoryNo}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long boardCategoryNo) {
       categoryService.deleteCategory(boardCategoryNo);
       return ResponseEntity.accepted().build();
    }

}
