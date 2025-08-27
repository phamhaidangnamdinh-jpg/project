package com.Chicken.project.service;

import com.Chicken.project.dto.request.Category.CategoryRequest;
import com.Chicken.project.dto.response.Book.ShortBookResponse;
import com.Chicken.project.dto.response.Borrow.BorrowResponse;
import com.Chicken.project.dto.response.Category.CategoryResponse;
import com.Chicken.project.dto.response.Category.CategoryStatisticResponse;
import com.Chicken.project.dto.response.Category.ShortCategoryResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.Category;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryService {
    ShortCategoryResponse toShortResponse(Category cat);
    CategoryResponse toResponse(Category category);
    boolean deleteCategory(long id);
    List<ShortCategoryResponse> getCategories();
    CategoryResponse getDetail(long id);
    CategoryResponse createCategory(CategoryRequest res);
    CategoryResponse updateCategory(CategoryRequest res);

    List<CategoryStatisticResponse> getBookStatistic();
    PageResponse<ShortCategoryResponse> filter(@RequestParam(required = false) String code,
                                                          @RequestParam(required = false) String description,
                                                          @RequestParam(required = false) String name,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size);
}
