package com.Chicken.project.dto.request.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "{category.code.notEmpty}")
    private String code;

    @NotBlank(message = "{category.name.notEmpty}")
    private String name;

    @NotBlank(message = "{category.description.notEmpty}")
    private String description;
}
