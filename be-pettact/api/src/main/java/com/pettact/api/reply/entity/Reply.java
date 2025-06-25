package com.pettact.api.reply.entity;


import com.pettact.api.board.entity.Board;
import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.reply.dto.ReplyRequestDto;
import com.pettact.api.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_no")
    private Long replyNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private Users user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reply_no")
    private Reply parentReply;

    @OneToMany(mappedBy = "parentReply")
    private List<Reply> childReplies = new ArrayList<>();


    public Reply(Board board, Reply parentReply, Users user, String content) {
        super();
        this.board = board;
        this.parentReply = parentReply;
        this.user = user;
        this.content = content;
    }

    public void updateContent(ReplyRequestDto replyRequestDto) {
        if (replyRequestDto.getContent() != null) {
            this.content = replyRequestDto.getContent();
        }
    }

    public void markAsDeleted() {
        this.content = "삭제된 댓글입니다.";
        softDelete();
    }
}
