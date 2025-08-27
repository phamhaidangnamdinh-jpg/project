package com.Chicken.project.controller;

import com.Chicken.project.config.ResponseConfig;
import com.Chicken.project.dto.request.Function.FunctionFilterDto;
import com.Chicken.project.dto.request.Function.FunctionRequest;
import com.Chicken.project.dto.request.Function.FunctionUpdateRequest;
import com.Chicken.project.dto.request.User.UserNameRequest;
import com.Chicken.project.dto.response.ApiResponse;
import com.Chicken.project.dto.response.Function.FunctionResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.service.impl.FunctionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/library/permission")
public class PermissionController {
    private final ResponseConfig responseConfig;
    @Autowired
    private FunctionServiceImpl service;
    @GetMapping("")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<?>>> getPermissions(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "5") int size) {
        return responseConfig.success("ROLE_VIEW_PERMISSION", "permission.view.success", null, service.getAll(page, size));
    }
    @GetMapping("{id}/detail")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<FunctionResponse>> getDetail(HttpServletRequest request,@PathVariable long id) {
        return responseConfig.success("ROLE_VIEW_PERMISSION", "permission.detail.success", new Object[]{id}, service.getById(id));
    }
    @PostMapping("create")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<FunctionResponse>> createRole(@Valid @RequestBody FunctionRequest req, HttpServletRequest request) {
        return responseConfig.success("ROLE_CREATE_PERMISSION", "permission.create.success",null, service.create(req));
    }
    @PutMapping("{id}/update")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<FunctionResponse>> updateRole(@RequestBody FunctionUpdateRequest req, @PathVariable long id, HttpServletRequest request) {
        return responseConfig.success("ROLE_UPDATE_PERMISSION", "permission.update.success", new Object[]{id}, service.update(id, req));
    }
    @DeleteMapping("{id}/delete")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<FunctionResponse>> deleteRole(@PathVariable long id, HttpServletRequest request) {
        service.delete(id);
        return responseConfig.success("ROLE_DELETE_PERMISSION", "permission.delete.success", new Object[]{id}, null);
    }
    @GetMapping("find-by-user")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<List<?>>> getRoles(HttpServletRequest request, UserNameRequest req) {
        return responseConfig.success("ROLE_VIEW_PERMISSION", "permission.view-by-user.success", new Object[]{req.getUsername()}, service.findByUser(req));
    }
    @GetMapping("filter")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<FunctionResponse>>> filter(HttpServletRequest request, @RequestParam(required = false) String functionCode,
                                                            @RequestParam(required = false) String functionName,
                                                            @RequestParam(required = false) String description,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "5") int size) {
        return responseConfig.success("ROLE_VIEW_PERMISSION", "permission.filter.success", null, service.FILTER_FUNCTION(functionCode,functionName,description,page,size));
    }
    @GetMapping("search")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<FunctionResponse>>> filter(HttpServletRequest request, @RequestBody FunctionFilterDto filter,
                                                                              @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "5") int size) {
        return responseConfig.success("ROLE_VIEW_PERMISSION", "permission.filter.success", null, service.FILTER_FUNCTION_DTO(filter,page,size));
    }
}

