package com.Chicken.project.dto.response.Book;

import lombok.Data;

@Data
public class ShortBookResponse {
    private Long id;
    private String code;
    private String title;
    private String author;
}
