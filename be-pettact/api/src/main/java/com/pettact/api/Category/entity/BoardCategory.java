package com.pettact.api.Category.entity;


import com.pettact.api.board.entity.Board;
import com.pettact.api.Category.dto.CreateDto;
import com.pettact.api.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_category_no")
    private Long boardCategoryNo;

    // Board와 1:N 관계 - category 삭제 시 "remove" 게시글 모두 삭제
    @OneToMany(mappedBy = "boardCategory", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Board> boards = new ArrayList<>();

    @Column(name = "boaard_category_title", nullable = false, length = 255)
    private String boardCategoryTitle;

    @Column(name = "board_category_description", columnDefinition = "TEXT")
    private String boardCategoryDescription;

    // "ROLE_ADMIN", "ROLE_USER" 등
    @Column(name = "board_allow_role", nullable = false)
    private String boardAllowedRole;

    @Column(name = "board_allow_image", nullable = false)
    private Boolean boardAllowImage;

    @Column(name = "board_allow_attachment", nullable = false)
    private Boolean boardAllowAttachment;

    @Column(name = "board_max_file_size", nullable = false)
    private Integer boardMaxFileSize;

    @Column(name = "board_allow_reply", nullable = false)
    private Boolean boardAllowReply;

    @Column(name = "board_max_image_count")
    private Integer boardMaxImageCount;

    @Column(name = "editor_type", nullable = false)
    private String editorType;

    @Column(name = "board_allow_recommend", nullable = false)
    private Boolean boardAllowRecommend;

    public BoardCategory(String boardCategoryTitle, String boardCategoryDescription, String boardAllowedRole, Boolean boardAllowAttachment,
                         Boolean boardAllowImage, Boolean boardAllowReply, Boolean boardAllowRecommend, Integer boardMaxFileSize, Integer boardMaxImageCount, String editorType) {
        super();
        this.boardCategoryTitle = boardCategoryTitle;
        this.boardCategoryDescription = boardCategoryDescription;
        this.boardAllowedRole = boardAllowedRole;
        this.boardAllowAttachment = boardAllowAttachment;
        this.boardAllowImage = boardAllowImage;
        this.boardAllowReply = boardAllowReply;
        this.boardAllowRecommend = boardAllowRecommend;
        this.boardMaxFileSize = boardMaxFileSize;
        this.boardMaxImageCount = boardMaxImageCount;
        this.editorType = editorType;
    }

    public void patchCategoryField(CreateDto createDto) {
       if (createDto.getBoardCategoryTitle() != null) {
           this.boardCategoryTitle = createDto.getBoardCategoryTitle();
       }
       if (createDto.getBoardCategoryDescription() != null) {
           this.boardCategoryDescription = createDto.getBoardCategoryDescription();
       }
       if (createDto.getBoardAllowImage() != null) {
           this.boardAllowImage = createDto.getBoardAllowImage();
       }
       if (createDto.getBoardAllowAttachment() != null) {
           this.boardAllowAttachment = createDto.getBoardAllowAttachment();
       }
       if (createDto.getBoardMaxFileSize() != null) {
           this.boardMaxFileSize = createDto.getBoardMaxFileSize();
       }
       if (createDto.getBoardMaxImageCount() != null) {
           this.boardMaxImageCount = createDto.getBoardMaxImageCount();
       }
       if (createDto.getBoardAllowReply() != null) {
           this.boardAllowReply = createDto.getBoardAllowReply();
       }
      if (createDto.getEditorType() != null) {
          this.editorType = createDto.getEditorType();
      }
      if (createDto.getBoardAllowRecommend() != null) {
          this.boardAllowRecommend = createDto.getBoardAllowRecommend();
      }
    }
}
