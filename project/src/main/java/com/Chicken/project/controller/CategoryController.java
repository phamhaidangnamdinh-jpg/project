package com.Chicken.project.controller;


import com.Chicken.project.config.ResponseConfig;
import com.Chicken.project.dto.request.Category.AddOrRemoveCategoryRequest;
import com.Chicken.project.dto.request.Category.CategoryFilterDto;
import com.Chicken.project.dto.request.Category.CategoryRequest;
import com.Chicken.project.dto.request.Category.CategoryUpdateRequest;
import com.Chicken.project.dto.response.ApiResponse;
import com.Chicken.project.dto.response.Category.CategoryResponse;
import com.Chicken.project.dto.response.Category.CategoryStatisticResponse;
import com.Chicken.project.dto.response.Category.ShortCategoryResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.service.impl.CategoryServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/library/category")
public class CategoryController {
    @Autowired
    CategoryServiceImpl service;
    private final ResponseConfig responseConfig;
    @GetMapping("")
//    @PreAuthorize("hasAuthority('ROLE_VIEW_CATEGORY')")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<?>>> getCategories(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "5") int size) {
        return responseConfig.success("ROLE_VIEW_CATEGORY", "category.view.success", null, service.getCategories(page, size));
    }
    @GetMapping("{id}/detail")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryDetail(HttpServletRequest request, @PathVariable long id) {
        return responseConfig.success("ROLE_VIEW_CATEGORY", "category.detail.success", new Object[]{id}, service.getDetail(id));
    }
    @PostMapping("/create")
//    @PreAuthorize("hasAuthority('ROLE_CREATE_CATEGORY')")
    @PreAuthorize("fileRole(#req)")
    public ResponseEntity<ApiResponse<CategoryResponse>> addCategory(HttpServletRequest req ,@Valid @RequestBody CategoryRequest request) {
        return responseConfig.success("ROLE_CREATE_CATEGORY", "category.create.success", null, service.createCategory(request));
    }
    @DeleteMapping("{id}/delete")
//    @PreAuthorize("hasAuthority('ROLE_DELETE_CATEGORY')")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<Objects>> deleteCategory(HttpServletRequest request ,@PathVariable Long id) {
        if (service.deleteCategory(id)) {
            return responseConfig.success("ROLE_DELETE_CATEGORY", "category.delete.success", new Object[]{id},null);
        }
        else return responseConfig.error("ROLE_DELETE_CATEGORY", "category.delete.failure");
    }
    @PutMapping("{id}/update")
//    @PreAuthorize("hasAuthority('ROLE_UPDATE_CATEGORY')")
    @PreAuthorize("fileRole(#req)")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(HttpServletRequest req ,@PathVariable Long id, @RequestBody CategoryUpdateRequest request) {
        return responseConfig.success("ROLE_UPDATE_CATEGORY", "category.update.success", new Object[]{id}, service.updateCategory(request, id));
    }

    @PutMapping("/{id}/update-books")
    @PreAuthorize("fileRole(#req)")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategoryBooks(HttpServletRequest req, @PathVariable Long id, @RequestBody AddOrRemoveCategoryRequest request){
        return responseConfig.success("ROLE_UPDATE_CATEGORY", "category.update.success", new Object[]{id}, service.AddOrRemoveFromCategory(request, id));
    }

    @GetMapping("statistics")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<List<CategoryStatisticResponse>>> getStatistics(HttpServletRequest request){
        return responseConfig.success("ROLE_VIEW_CATEGORY", "category.statistic.success", null, service.getBookStatistic());
    }
    @GetMapping("filter")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<ShortCategoryResponse>>> filter(HttpServletRequest request, @RequestParam(required = false) String code,
                                                                                   @RequestParam(required = false) String description,
                                                                                   @RequestParam(required = false) String name,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "5") int size){
        return responseConfig.success("ROLE_VIEW_CATEGORY", "category.filter.success", null, service.filter(code, description,name, page, size));
    }
    @GetMapping("search")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<ShortCategoryResponse>>> filter(HttpServletRequest request, CategoryFilterDto filter,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "5") int size){
        return responseConfig.success("ROLE_VIEW_CATEGORY", "category.filter.success", null, service.search(filter, page, size));
    }
}
