package com.pettact.api.board.entity;

import com.pettact.api.board.dto.BoardCreateDto;
import com.pettact.api.Category.entity.BoardCategory;
import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.reply.entity.Reply;
import com.pettact.api.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_category_no")
    private BoardCategory boardCategory;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply>replies;

    @Column(name="board_title", nullable = false, length = 100)
    private String boardTitle;

    @Column(name = "board_content", nullable = false, columnDefinition = "TEXT")
    private String boardContent;
    
    @Column(name = "board_view_cnt")
    private int boardViewCnt=0;


    public Board(String boardTitle, String boardContent, BoardCategory boardCategory, Users user) {
        super();
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardCategory = boardCategory;
        this.user = user;

    }

    public void updateBoard(BoardCreateDto boardCreateDto) {
        this.boardTitle = boardCreateDto.getBoardTitle();
        this.boardContent = boardCreateDto.getBoardContent();
    }

    // TODO: reply count 예정
}


