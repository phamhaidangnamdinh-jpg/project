package com.Chicken.project.dto.response.Category;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryStatisticResponse {
    private String category;
    private Long count;
}
