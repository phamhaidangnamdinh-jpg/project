package com.Chicken.project.dto.response.Article;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopLikedArticleResponse {
    private String title;
    private String authorName;
    int likeCount;
}
