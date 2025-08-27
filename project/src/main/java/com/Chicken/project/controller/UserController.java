package com.Chicken.project.controller;

import com.Chicken.project.config.ResponseConfig;
import com.Chicken.project.dto.request.User.LoginRequest;
import com.Chicken.project.dto.request.User.UserFilterDto;
import com.Chicken.project.dto.request.User.UserRequest;
import com.Chicken.project.dto.request.User.UserUpdateRequest;
import com.Chicken.project.dto.response.ApiResponse;
import com.Chicken.project.dto.response.LoginResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.dto.response.User.UserResponseForAdmin;
import com.Chicken.project.dto.response.User.UserShortResponse;
import com.Chicken.project.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/library/user")
public class UserController {
    private final ResponseConfig responseConfig;
    @Autowired
    private UserServiceImpl service;

    @GetMapping("")
//    @PreAuthorize("hasAuthority('ROLE_VIEW_USER')")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<UserShortResponse>>> viewUser(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        return responseConfig.success("ROLE_VIEW_USER", "user.view.success", null, service.viewUser(page, size));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseForAdmin>> registerUser(@Valid @RequestBody UserRequest request) {
        return responseConfig.success("ROLE_CREATE_USER","user.create.success", null, service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody LoginRequest request) {
        return responseConfig.success("ROLE_LOGIN_USER", "user.login.success",null ,service.verify(request));
    }


    @PostMapping("/create")
    @PreAuthorize("fileRole(#req)")
    public ResponseEntity<ApiResponse<UserResponseForAdmin>> createUser(HttpServletRequest req, @RequestBody UserRequest request) {
        System.out.println(request.getPassword());
        return responseConfig.success("ROLE_CREATE_USER","user.create.success", null, service.createUser(request));
    }

    @GetMapping("export")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<InputStreamResource> exportUser(HttpServletRequest request) throws IOException {
        return responseConfig.downloadFile("books.csv", service.exportToCsv());
    }
    @GetMapping("export-excel")
    @PreAuthorize("fileRole(#request)")
    public void exportUserExcel(@RequestBody(required = false) UserFilterDto filter,
                                                               HttpServletResponse response, HttpServletRequest request
    ) throws IOException {
        service.exportToExcel(filter, response);
    }

    @PutMapping("/{id}/update")
//    @PreAuthorize("hasAuthority('ROLE_VIEW_USER')")
    @PreAuthorize("fileRole(#req) or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserResponseForAdmin>> updateUser(HttpServletRequest req, @PathVariable long id,@Valid @RequestBody UserUpdateRequest request) {
        if(request!=null && service.updateUser(id, request)!=null) {
            return responseConfig.success("ROLE_UPDATE_USER","user.update.success", new Object[]{id}, service.updateUser(id, request));
        }
        return responseConfig.error("ROLE_UPDATE_USER", "user.update.failure");
    }
    @GetMapping("/{id}/detail")
//    @PreAuthorize("hasAuthority('ROLE_VIEW_USER')")
    @PreAuthorize("fileRole(#req) or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserResponseForAdmin>> viewUserDetail(HttpServletRequest req, @PathVariable Long id) {
        UserResponseForAdmin user = service.viewUserDetail(id);
        if (user != null) {
            return responseConfig.success("ROLE_DETAIL_USER","user.detail.success", new Object[]{id}, user);
        }
        else return responseConfig.error("ROLE_DETAIL_USER", "user.detail.failure");
    }

    @GetMapping("/detail/search-key")
//    @PreAuthorize("hasAuthority('ROLE_VIEW_USER')")
    @PreAuthorize("fileRole(#req)")
    public ResponseEntity<ApiResponse<List<UserShortResponse>>> searchWithKeyWord(HttpServletRequest req, @RequestParam String keyword) {
        List<UserShortResponse> list = service.search(keyword);
        return responseConfig.success("ROLE_SEARCH_USER","user.filter.success", null, list);
    }
    @DeleteMapping("{id}/delete")
//    @PreAuthorize("hasAuthority('ROLE_DELETE_USER')")
    @PreAuthorize("fileRole(#req) or #id == authentication.principal.id")
    @Operation(summary = "Delete a user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Object>> deleteUser(HttpServletRequest req,@PathVariable Long id) {
        if(service.deleteUser(id)) {
//            String message = "User " + id + " has been deleted";
            return responseConfig.success("ROLE_DELETE_USER", "user.delete.success", new Object[]{id}, null);
        }
        else return responseConfig.error("ROLE_DELETE_USER", "user.delete.failure");
    }
    @GetMapping("/filter")
//    @PreAuthorize("hasAuthority('ROLE_VIEW_USER')")
    @PreAuthorize("fileRole(#req)")
    public ResponseEntity<ApiResponse<PageResponse<UserShortResponse>>> filterUsers(HttpServletRequest req,
                                                                                    @RequestParam(required = false) String username,
                                                                                    @RequestParam(required = false) String email,
                                                                                    @RequestParam(required = false) String phone,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size
    ) {
        return responseConfig.success("ROLE_VIEW_USER","user.filter.success", null, service.filterUser(username, email, phone, page, size));
    }
    @GetMapping("search")
    @PreAuthorize("fileRole(#req)")
    public ResponseEntity<ApiResponse<PageResponse<UserShortResponse>>> searchUsers(HttpServletRequest req,
                                                                                    @RequestBody UserFilterDto filter,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size
    ) {
        return responseConfig.success("ROLE_VIEW_USER","user.filter.success", null, service.filterUserDto(filter, page, size));
    }
}
