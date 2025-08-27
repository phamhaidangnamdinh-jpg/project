package com.Chicken.project.dto.request.Book;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
@Data
public class BookRequest {

    @NotBlank(message = "{book.code.notBlank}")
    private String code;

    @NotBlank(message = "{book.title.notBlank}")
    @Size(max = 255, message = "{book.title.maxSize}")
    private String title;

    @NotBlank(message = "{book.author.notBlank}")
    private String author;

    @NotBlank(message = "{book.publisher.notBlank}")
    private String publisher;

    @NotNull(message = "{book.pageCount.notNull}")
    @Min(value = 1, message = "{book.pageCount.min}")
    private Integer pageCount;

    @NotBlank(message = "{book.printType.notBlank}")
    private String printType;

    @NotBlank(message = "{book.language.notBlank}")
    private String language;

    @NotBlank(message = "{book.description.notBlank}")
    private String description;

    @NotNull(message = "{book.quantity.notNull}")
    @Min(value = 0, message = "{book.quantity.min}")
    private Integer quantity;

    @NotEmpty(message = "{book.categoryIds.notEmpty}")
    private List<Long> categoryIds;
}
