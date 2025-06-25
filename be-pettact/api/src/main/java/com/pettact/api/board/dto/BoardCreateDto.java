package com.pettact.api.board.dto;


import com.pettact.api.board.entity.Board;
import com.pettact.api.Category.entity.BoardCategory;
import com.pettact.api.user.entity.Users;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardCreateDto {

    private Long boardCategoryNo;
    private Long userNo;
    private String boardTitle;
    private String boardContent;

    public static Board toEntity(BoardCreateDto boardCreateDto, BoardCategory boardCategory, Users users) {
        return new Board(
                boardCreateDto.getBoardTitle(),
                boardCreateDto.getBoardContent(),
                boardCategory,
                users
        );
    }
}
