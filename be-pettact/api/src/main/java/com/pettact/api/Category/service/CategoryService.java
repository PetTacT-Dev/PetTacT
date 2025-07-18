package com.pettact.api.Category.service;

import com.pettact.api.Category.dto.CreateDto;
import com.pettact.api.Category.dto.ResponseDto;
import com.pettact.api.Category.entity.BoardCategory;
import com.pettact.api.Category.repository.CategoryRepository;
import com.pettact.api.board.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BoardRepository boardRepository;

    public ResponseDto createCategory(CreateDto createDto) {
        /*
         - TODO: 중복 체크 추가 예정
         - TODO: 권한 체크(admin)는 인증 모듈 완성 후 추가 예정 (!.isPresent())
         */
        if (categoryRepository.existsByBoardCategoryTitle(createDto.getBoardCategoryTitle())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다");
        }

        BoardCategory category = createDto.toEntity();
        BoardCategory saved = categoryRepository.save(category);
        return ResponseDto.fromEntity(saved);
    }

    public List<ResponseDto> getAllCategory() {
        List<BoardCategory> categories = categoryRepository.findAll();
        return categories.stream()
                .map(ResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 수정 예정
    public ResponseDto getCategoryByNo(Long boardCategoryNo) {
        BoardCategory category = categoryRepository.findById(boardCategoryNo)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. No: " + boardCategoryNo));

        // 해당 카테고리의 게시글 수 조회
        int boardCount = boardRepository.countByCategoryNo(boardCategoryNo);

        ResponseDto responseDto = ResponseDto.fromEntity(category);
        responseDto.setTotalBoards(boardCount);

        return responseDto;
    }

    @Transactional
    public ResponseDto updateCategory(Long boardCategoryNo, CreateDto createDto) {
        BoardCategory category = categoryRepository.findById(boardCategoryNo)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. No: " + boardCategoryNo));

        category.patchCategoryField(createDto);
        BoardCategory updated = categoryRepository.save(category);

        return ResponseDto.fromEntity(updated);
    }

    public void deleteCategory(Long boardCategoryNo) {
        if (!categoryRepository.existsById(boardCategoryNo)) {
            throw new IllegalArgumentException("카테고리를 찾을 수 없습니다. No: " + boardCategoryNo);
        }
        categoryRepository.deleteById(boardCategoryNo);
    }
}
