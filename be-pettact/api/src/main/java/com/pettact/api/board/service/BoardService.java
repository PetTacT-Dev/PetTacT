package com.pettact.api.board.service;

import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.board.dto.BoardCreateDto;
import com.pettact.api.board.dto.BoardResponseDto;
import com.pettact.api.Category.entity.BoardCategory;
import com.pettact.api.Category.repository.CategoryRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BoardResponseDto createBoard(BoardCreateDto boardCreateDto) {
        // TODO: user 조회 검증
        Users users = userRepository.findById(boardCreateDto.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        BoardCategory boardCategory = categoryRepository
                .findById(boardCreateDto.getBoardCategoryNo())
                .orElseThrow(() -> new RuntimeException("게시글 카테고리가 존재하지 않습니다."));

        Board board = BoardCreateDto.toEntity(boardCreateDto, boardCategory, users);
        Board savedBoard = boardRepository.save(board);
        return BoardResponseDto.fromEntity(savedBoard);
    }

    public List<BoardResponseDto> getAllBoard() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(BoardResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public BoardResponseDto getBoardByNo(Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글 정보를 찾을 수 없습니다. No: " + boardNo));
        return BoardResponseDto.fromEntity(board);
    }

    public BoardResponseDto updateBoard(Long boardNo, BoardCreateDto boardCreateDto) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. No: " + boardNo));

        board.updateBoard(boardCreateDto);
        Board updated = boardRepository.save(board);

        return BoardResponseDto.fromEntity(updated);
    }

    public void deleteBoard(Long boardNo) {
        if (!boardRepository.existsById(boardNo)) {
            throw new IllegalArgumentException("게시글 정보를 찾을 수 럾습니다. No: " + boardNo);
        }
        boardRepository.deleteById(boardNo);
    }
}
