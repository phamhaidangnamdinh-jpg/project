package com.Chicken.project.dto.request.Category;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class CategoryFilterDto {
    private String code;
    private String description;
    private String name;
}
