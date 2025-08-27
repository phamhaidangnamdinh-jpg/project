package com.Chicken.project.dto.response.Article;

import com.Chicken.project.dto.response.Comment.CommentResponse;
import lombok.Data;

import java.util.List;

@Data
public class ShortArticleResponse {
    private String title;
    int likeCount;
    int DislikeCount;
}
