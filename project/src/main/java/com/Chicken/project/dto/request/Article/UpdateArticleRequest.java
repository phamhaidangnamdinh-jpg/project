package com.Chicken.project.dto.request.Article;

import lombok.Data;

@Data
public class UpdateArticleRequest {
    private String title;
    private String content;
}
