package com.pettact.api.board.service;

import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.common.ViewCountService;
import com.pettact.api.common.scheduler.ViewCountScheduler.ViewCountSyncable;
import com.pettact.api.board.dto.BoardCreateDto;
import com.pettact.api.board.dto.BoardResponseDto;
import com.pettact.api.Category.entity.BoardCategory;
import com.pettact.api.Category.repository.CategoryRepository;
import com.pettact.api.file.dto.FileDto;
import com.pettact.api.file.entity.File;
import com.pettact.api.file.service.MultiFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import com.pettact.api.recommend.boardRecommend.repository.BoardRecommendRepository;
import com.pettact.api.reply.dto.ReplyResponseDto;
import com.pettact.api.reply.service.ReplyService;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class BoardService implements ViewCountSyncable<Long> {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRecommendRepository boardRecommendRepository;
    @Autowired
    private ViewCountService viewCountService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MultiFileService multiFileService;


    @Transactional
    public BoardResponseDto createBoard(BoardCreateDto boardCreateDto,  Long userNo) {
        Users users = userRepository.findById(userNo)
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
                .map(board -> {
                    BoardResponseDto dto = BoardResponseDto.getAllBoard(board);

                    // 댓글 수 설정
                    int replyCount = replyService.countByBoardNo(board.getBoardNo());
                    System.out.println("Board " + board.getBoardNo() + " 댓글 수: " + replyCount);
                    dto.setTotalReplyCount(replyCount);

                    // 게시글 추천 수 설정
                    int recommendCount = boardRecommendRepository.countByBoardNo(board.getBoardNo());
                    dto.setBoardRecommendCount(recommendCount);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoardByNo(Long boardNo, String sessionId) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글 정보를 찾을 수 없습니다. No: " + boardNo));
        String preventKey = "board:viewed:" + sessionId + ":" + boardNo;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(preventKey))) {
            viewCountService.increaseViewCount("board", boardNo, 120);
            redisTemplate.opsForValue().set(preventKey, "1", Duration.ofMinutes(60));
        }

        // 게시글 엔티티에서 조회수 가져오고, Redis에 누적된 조회수를 더함
        String redisKey = "board:views:" + boardNo;
        String redisCountStr = redisTemplate.opsForValue().get(redisKey);
        int redisCount = (redisCountStr != null) ? Integer.parseInt(redisCountStr) : 0;


        Pageable pageable = PageRequest.of(0, 10);
        Page<ReplyResponseDto> replyPage = replyService.getAllReplies(boardNo, pageable);
        List<ReplyResponseDto> replyList = replyPage.getContent();

        // 인기 댓글
        List<ReplyResponseDto> topReplies = replyList.stream()
                .filter(reply -> reply.getRecommendCount() >= 1)
                .limit(4)
                .collect(Collectors.toList());

        List<ReplyResponseDto> normalReplies = replyList.stream()
                .filter(reply -> reply.getRecommendCount() < 1)
                .collect(Collectors.toList());

        int recommendCount = boardRecommendRepository.countByBoardNo(boardNo);
        BoardResponseDto boardResponseDto = BoardResponseDto.fromEntity(board);
        // DTO에 합산된 조회수 설정
        boardResponseDto.setBoardViewCnt(board.getBoardViewCnt() + redisCount);

        // 인기 댓글과 일반 댓글을 분리해서 설정
        boardResponseDto.setTopReplies(topReplies);      // 인기 댓글
        boardResponseDto.setNormalReplies(normalReplies); // 일반 댓글
        boardResponseDto.setBoardRecommendCount(recommendCount);

        return boardResponseDto;
    }

    @Transactional
    public BoardResponseDto updateBoard(
            Long boardNo,
            BoardCreateDto boardCreateDto,
            Long userNo,
            MultipartFile[] files,
            List<Long> deletedFileIds
    ) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. No: " + boardNo));

        if (!board.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("수정 권한이 없습니다. 작성자만 수정할 수 있습니다.");
        }

        board.updateBoard(boardCreateDto);
        boardRepository.save(board);

        log.info("삭제 요청된 파일 ID 목록: {}", deletedFileIds);

        if (deletedFileIds != null && !deletedFileIds.isEmpty()) {
            for (Long fileNo : deletedFileIds) {
                log.info("파일 삭제 호출됨: fileNo={}, userNo={}", fileNo, userNo);
                multiFileService.delete(fileNo, userNo);
                log.info("파일 삭제 완료: fileNo={}", fileNo);
            }
            boardRepository.flush();
        }

        if (files != null && files.length > 0) {
            multiFileService.createFiles(
                    File.ReferenceTable.BOARD,
                    boardNo,
                    files,
                    userNo
            );
        }

        BoardResponseDto responseDto = BoardResponseDto.fromEntity(board);
        List<FileDto> uploadedFiles = multiFileService.getFilesByReference(File.ReferenceTable.BOARD, boardNo);
        responseDto.setFiles(uploadedFiles);

        return responseDto;
    }

    @Transactional
    public void deleteBoard(Long boardNo, Long userNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글 정보를 찾을 수 없습니다. No: " + boardNo));

        if (!board.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("해당 사용자가 아닙니다. 삭제를 할 수 없습니다.");
        }

        boardRepository.delete(board);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> findBoardsByCategory(Long categoryNo, Pageable pageable) {
        return findBoardsByCategory(categoryNo, pageable, null);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> findBoardsByCategory(Long categoryNo, Pageable pageable, String searchKeyword) {
        System.out.println("🔍 서비스 호출:");
        System.out.println("  - categoryNo: " + categoryNo);
        System.out.println("  - pageable: " + pageable);
        System.out.println("  - searchKeyword: [" + searchKeyword + "]");

        BoardCategory boardCategory = categoryRepository.findById(categoryNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다. No: " + categoryNo));

        Page<Board> boards;

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            System.out.println("✅ 검색 실행");
            boards = boardRepository.searchBoardsByCategory(categoryNo, searchKeyword.trim(), pageable);
        } else {
            System.out.println("✅ 일반 조회 실행");
            boards = boardRepository.findBoardsByCategoryNo(categoryNo, pageable);
        }

        System.out.println("📊 Repository 결과:");
        System.out.println("  - totalElements: " + boards.getTotalElements());
        System.out.println("  - totalPages: " + boards.getTotalPages());
        System.out.println("  - currentPage: " + boards.getNumber());
        System.out.println("  - pageSize: " + boards.getSize());

        return boards.map(board -> {
            BoardResponseDto dto = BoardResponseDto.fromEntity(board);
            int replyCount = replyService.countByBoardNo(board.getBoardNo());
            dto.setTotalReplyCount(replyCount);
            return dto;
        });
    }

    // ------------------ 게시글 조회수 db 갱신------------------

    @Override
    @Transactional
    public void updateViewCount(Long boardNo, int count) {
        boardRepository.updateViewCount(boardNo, count);
    }

    // ------------------ 인기 게시글 TOP 10 ------------------
    public List<BoardResponseDto> getPopularBoards(Long categoryNo, int count) {
        List<String> dateKeys = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> {
                    String date = LocalDate.now().minusDays(i).toString();
                    return (categoryNo == null)
                            ? "board:popular:" + date
                            : "board:popular:" + categoryNo + ":" + date;
                })
                .toList();

        String tempKey = "board:popular:temp:" + UUID.randomUUID();
        redisTemplate.opsForZSet().unionAndStore(dateKeys.get(0), dateKeys.subList(1, dateKeys.size()), tempKey);

        Set<String> boardNos = redisTemplate.opsForZSet()
                .reverseRange(tempKey, 0, count - 1);

        redisTemplate.delete(tempKey);

        if (boardNos == null || boardNos.isEmpty()) return List.of();

        List<Long> ids = boardNos.stream().map(Long::parseLong).toList();
        List<Board> boards = boardRepository.findAllById(ids);

        Map<Long, Board> boardMap = boards.stream()
                .collect(Collectors.toMap(Board::getBoardNo, Function.identity()));

        return ids.stream()
                .map(boardMap::get)
                .filter(Objects::nonNull)
                .map(BoardResponseDto::fromEntity)
                .toList();
    }

}