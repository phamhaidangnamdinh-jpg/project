package com.Chicken.project.dto.response.Article;

import com.Chicken.project.dto.response.Comment.CommentResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ArticleResponse {
    private String title;
    private String authorName;
    private String content;
    private String bookName;
    private List<CommentResponse> comments;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate updateDate;
    int likeCount;
    int DislikeCount;
}
