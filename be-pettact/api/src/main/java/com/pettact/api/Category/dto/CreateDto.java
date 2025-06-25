package com.pettact.api.Category.dto;

import com.pettact.api.Category.entity.BoardCategory;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateDto {

    private String boardCategoryTitle;
    private String boardCategoryDescription;
    private String boardAllowedRole;
    private Boolean boardAllowAttachment;
    private Boolean boardAllowImage;
    private Boolean boardAllowReply;
    private Boolean boardAllowRecommend;
    private Integer boardMaxFileSize;
    private Integer boardMaxImageCount;
    private String editorType;

    public BoardCategory toEntity() {
        return new BoardCategory(
                boardCategoryTitle,
                boardCategoryDescription,
                boardAllowedRole,
                boardAllowAttachment,
                boardAllowImage,
                boardAllowReply,
                boardAllowRecommend,
                boardMaxFileSize,
                boardMaxImageCount,
                editorType
        );
    }
}
