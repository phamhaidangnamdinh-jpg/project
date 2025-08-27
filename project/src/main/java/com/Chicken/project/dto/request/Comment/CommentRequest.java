package com.Chicken.project.dto.request.Comment;

import lombok.Data;

@Data
public class CommentRequest {
    private String comment;
    private long ArticleId;
}
