package com.Chicken.project.controller;


import com.Chicken.project.config.ResponseConfig;
import com.Chicken.project.dto.request.Role.RoleFilterDto;
import com.Chicken.project.dto.request.Role.RoleGroupRequest;
import com.Chicken.project.dto.request.Role.RoleGroupUpdateRequest;
import com.Chicken.project.dto.request.Role.UserToAddRole;
import com.Chicken.project.dto.response.ApiResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.dto.response.Role.RoleGroupResponse;
import com.Chicken.project.service.impl.RoleGroupServiceImpl;
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
@RequestMapping("/api/v1/library/role")
public class RoleController {
    private final ResponseConfig responseConfig;
    @Autowired
    private RoleGroupServiceImpl service;
    @GetMapping("")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<?>>> getRoles(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        return responseConfig.success("ROLE_VIEW_ROLE_GROUP", "role.view.success", null, service.getAll(page, size));
    }
    @GetMapping("{id}/detail")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<RoleGroupResponse>> getRoleDetail(HttpServletRequest request,@PathVariable long id) {
        return responseConfig.success("ROLE_VIEW_ROLE_GROUP", "role.detail.success", new Object[]{id}, service.getById(id));
    }
    @PostMapping("create")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<RoleGroupResponse>> createRole(@Valid @RequestBody RoleGroupRequest req, HttpServletRequest request) {
        return responseConfig.success("ROLE_CREATE_ROLE_GROUP", "role.create.success", null, service.create(req));
    }
    @PutMapping("{id}/update")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<RoleGroupResponse>> updateRole(@RequestBody RoleGroupUpdateRequest req, @PathVariable long id, HttpServletRequest request) {
        return responseConfig.success("ROLE_UPDATE_ROLE_GROUP", "role.update.success", new Object[]{id}, service.update(id, req));
    }
    @PutMapping("add-user")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<RoleGroupResponse>> updateRole(@RequestBody UserToAddRole req, HttpServletRequest request) {
        return responseConfig.success("ROLE_UPDATE_ROLE_GROUP", "role.update.success", null, service.addUser(req));
    }
    @DeleteMapping("{id}/delete")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<RoleGroupResponse>> deleteRole(@PathVariable long id, HttpServletRequest request) {
        service.delete(id);
        return responseConfig.success("ROLE_DELETE_ROLE_GROUP", "role.delete.success", new Object[]{id}, null);
    }
    @GetMapping("filter")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<RoleGroupResponse>>> getRoleDetail(HttpServletRequest request, @RequestParam(required = false) String roleGroupCode,
                                                                                      @RequestParam(required = false) String roleGroupName,
                                                                                      @RequestParam(required = false) String description,
                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "10") int size) {
        return responseConfig.success("ROLE_VIEW_ROLE_GROUP", "role.filter.success", null, service.FILTER_ROLE(roleGroupCode,roleGroupName,description,page,size));
    }
    @GetMapping("search")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<RoleGroupResponse>>> getRoleDetail(HttpServletRequest request, @RequestBody RoleFilterDto filter,
                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "10") int size) {
        return responseConfig.success("ROLE_VIEW_ROLE_GROUP", "role.filter.success", null, service.FILTER_ROLE_DTO(filter,page,size));
    }
}
