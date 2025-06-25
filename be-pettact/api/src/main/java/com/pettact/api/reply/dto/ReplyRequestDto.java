package com.pettact.api.reply.dto;


import com.pettact.api.board.entity.Board;
import com.pettact.api.reply.entity.Reply;
import com.pettact.api.user.entity.Users;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReplyRequestDto {

    // boardNo -> 매핑 url에서 추출
    private Long parentReplyNo;
    private Long userNo;
    private String content;


    public Reply toEntity(Board board, Reply parentReply, Users users) {
        return new Reply(
                board,
                parentReply,
                users,
                content
        );
    }
}
