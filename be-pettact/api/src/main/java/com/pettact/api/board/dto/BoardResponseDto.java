package com.pettact.api.board.dto;


import com.pettact.api.board.entity.Board;
import com.pettact.api.Category.dto.ResponseDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardResponseDto {

    private Long boardNo;
    private Long userNo;
    private ResponseDto responseDto;
    private String boardTitle;
    private String boardContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;


    public static BoardResponseDto fromEntity(Board savedBoard) {
        return new BoardResponseDto(
                savedBoard.getBoardNo(),
                savedBoard.getUserNo().getUserNo(),
                ResponseDto.fromEntity(savedBoard.getBoardCategory()),
                savedBoard.getBoardTitle(),
                savedBoard.getBoardContent(),
                savedBoard.getCreatedAt(),
                savedBoard.getUpdatedAt(),
                savedBoard.isDeleted(),
                savedBoard.getDeletedAt()
        );
    }
}
