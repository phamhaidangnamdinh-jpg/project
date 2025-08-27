package com.Chicken.project.dto.request.Category;

import lombok.Data;

import java.util.List;

@Data
public class AddOrRemoveCategoryRequest {
    List<Long> addIds;
    List<Long> removeIds;
}
