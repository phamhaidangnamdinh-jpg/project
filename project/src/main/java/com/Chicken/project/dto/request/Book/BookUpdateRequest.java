package com.Chicken.project.dto.request.Book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BookUpdateRequest {
    private String code;
    @Size(max = 255, message = "{book.title.maxSize}")
    private String title;
    private String author;
    private String publisher;
    @Min(value = 1, message = "{book.pageCount.min}")
    private Integer pageCount;
    private String printType;
    private String language;
    private String description;
    @Min(value = 0, message = "{book.quantity.min}")
    private Integer quantity;
    private List<Long> categoryIds;
}
