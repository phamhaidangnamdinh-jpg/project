package com.Chicken.project.dto.request.Comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentCreateRequest {
    @NotBlank(message = "{comment.content.notEmpty}")
    private String content;

    @NotNull(message = "{comment.articleId.notEmpty}")
    private Long articleId;

    private Long parentId;
}
