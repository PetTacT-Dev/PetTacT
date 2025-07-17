package com.pettact.api.board.dto;


import com.pettact.api.board.entity.Board;
import com.pettact.api.Category.dto.ResponseDto;
import com.pettact.api.file.dto.FileDto;
import com.pettact.api.reply.dto.ReplyResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardResponseDto {

    private Long boardNo;
    private Long userNo;
    private String userNickname;
    private ResponseDto responseDto;
    private String boardTitle;
    private String boardContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private List<ReplyResponseDto> replies = new ArrayList<>();
    private int boardRecommendCount;
    private int totalReplyCount;
    private int boardViewCnt;
    private List<FileDto> files;
    private List<ReplyResponseDto> topReplies;
    private List<ReplyResponseDto> normalReplies;


    public static BoardResponseDto fromEntity(Board savedBoard) {
        return new BoardResponseDto(
                savedBoard.getBoardNo(),
                savedBoard.getUser().getUserNo(),
                savedBoard.getUser().getUserNickname(),
                ResponseDto.fromEntity(savedBoard.getBoardCategory()),
                savedBoard.getBoardTitle(),
                savedBoard.getBoardContent(),
                savedBoard.getCreatedAt(),
                savedBoard.getUpdatedAt(),
                savedBoard.isDeleted(),
                savedBoard.getDeletedAt(),
                new ArrayList<>(),
                0,
                0,
                savedBoard.getBoardViewCnt(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public static BoardResponseDto getAllBoard(Board board) {
        return new BoardResponseDto(
                board.getBoardNo(),
                board.getUser().getUserNo(),
                board.getUser().getUserNickname(),
                ResponseDto.fromEntity(board.getBoardCategory()),
                board.getBoardTitle(),
                board.getBoardContent(),
                board.getCreatedAt(),
                board.getUpdatedAt(),
                board.isDeleted(),
                board.getDeletedAt(),
                null,
                0,
                0,
                board.getBoardViewCnt(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}
