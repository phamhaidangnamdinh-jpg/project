package com.Chicken.project.dto.response.Comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FullCommentResponse {
    private String content;
    private String authorName;
    private String articleName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate updateDate;
    private Long parentId;
    private Long articleId;
    private long totalReplies;
    private List<ShortCommentResponse> replies;
}
