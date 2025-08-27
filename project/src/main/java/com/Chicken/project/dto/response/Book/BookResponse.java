package com.Chicken.project.dto.response.Book;

import com.Chicken.project.dto.response.Category.ShortCategoryResponse;
import lombok.Data;

import java.util.List;

@Data
public class BookResponse {
    private Long id;
    private String code;
    private String title;
    private String author;
    private String publisher;
    private int pageCount;
    private String printType;
    private String language;
    private String description;
    private int isBorrowed;
    private List<ShortCategoryResponse> categories;
}
