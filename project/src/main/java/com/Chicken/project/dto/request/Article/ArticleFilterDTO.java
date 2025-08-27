package com.Chicken.project.dto.request.Article;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Data
public class ArticleFilterDTO {
    private String title;
    private String author;
    private String bookName;
    private String content;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate createDateTo;
    @DateTimeFormat(pattern = "dd-MM-yyyy") private LocalDate createDateFrom;
}
