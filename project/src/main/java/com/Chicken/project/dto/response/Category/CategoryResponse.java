package com.Chicken.project.dto.response.Category;

import com.Chicken.project.dto.response.Book.ShortBookResponse;
import lombok.Data;

import java.util.List;

@Data
public class CategoryResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private List<ShortBookResponse> books;
}
