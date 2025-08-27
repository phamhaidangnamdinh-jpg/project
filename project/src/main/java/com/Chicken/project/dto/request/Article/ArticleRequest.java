package com.Chicken.project.dto.request.Article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleRequest {
    @NotBlank(message = "{article.title.notBlank}")
    @Size(max = 200, message = "{article.title.maxSize}")
    private String title;

    @NotBlank(message = "{article.content.notBlank}")
    private String content;

    @NotBlank(message = "{article.bookCode.notBlank}")
    private String bookCode;
}
