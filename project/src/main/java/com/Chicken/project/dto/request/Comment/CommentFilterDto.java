package com.Chicken.project.dto.request.Comment;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Data
public class CommentFilterDto {
    private String author;
    private String content;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate createDateTo;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate createDateFrom;
}
