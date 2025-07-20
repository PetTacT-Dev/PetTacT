package com.pettact.api.reply.dto;


import com.pettact.api.reply.entity.Reply;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReplyResponseDto {

    private Long replyNo;
    private Long boardNo;
    // user
    private Long userNo;
    private String userNickname;

    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // parentReply.getReplyNo
    private Long parentReplyNo;
    // Reply -> ReplyResponseDto로 변환
    private List<ReplyResponseDto> childReplies;
    private int recommendCount;
    private int depth;  // 댓글 깊이 (들여쓰기용)



    public static ReplyResponseDto fromEntity(Reply savedReply) {
        return new ReplyResponseDto(
                savedReply.getReplyNo(),
                savedReply.getBoard().getBoardNo(),
                savedReply.getUser().getUserNo(),
                savedReply.getUser().getUserNickname(),
                savedReply.getContent(),
                savedReply.getCreatedAt(),
                savedReply.getUpdatedAt(),
                savedReply.getParentReply() != null ? savedReply.getParentReply().getReplyNo() : null,
                new ArrayList<>(),
                0,
                0
        );
    }
}
