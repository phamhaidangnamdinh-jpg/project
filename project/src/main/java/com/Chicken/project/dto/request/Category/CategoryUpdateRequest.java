package com.Chicken.project.dto.request.Category;

import lombok.Data;

@Data
public class CategoryUpdateRequest {
    private String code;
    private String name;
    private String description;
}
