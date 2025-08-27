package com.Chicken.project.controller;


import com.Chicken.project.config.ResponseConfig;
import com.Chicken.project.dto.request.Borrow.BorrowCreateRequest;
import com.Chicken.project.dto.request.Borrow.BorrowFilterRequest;
import com.Chicken.project.dto.response.ApiResponse;
import com.Chicken.project.dto.response.Borrow.BorrowResponse;
import com.Chicken.project.dto.response.Category.ShortCategoryResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.UserPrincipal;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.service.impl.BorrowServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/library/borrow")
public class BorrowController {
    @Autowired
    BorrowServiceImpl service;
    private final ResponseConfig responseConfig;
    @GetMapping("")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<?>>> viewBorrow(@AuthenticationPrincipal UserPrincipal up, HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size) {
        V_User user = up.getUser();
        return responseConfig.success("ROLE_VIEW_BORROW", "borrow.view.success", null, service.viewBorrow(user, page, size));
    }
    @GetMapping("{id}/detail")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<BorrowResponse>> viewBorrowDetail(@AuthenticationPrincipal UserPrincipal up, HttpServletRequest request, long id) {
        V_User user = up.getUser();
        return responseConfig.success("ROLE_VIEW_BORROW", "borrow.detail.success", new Object[]{id}, service.viewBorrowDetail(user, id));
    }
    @PostMapping("/create")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<Object>> createBorrow(@Valid @RequestBody BorrowCreateRequest req, HttpServletRequest request){
        return responseConfig.success("ROLE_CREATE_BORROW", "borrow.create.success", null, service.createBorrow(req));
    }
    @DeleteMapping("{id}/delete")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<String>> deleteBorrow(@PathVariable long id, HttpServletRequest request){
        service.deleteBorrow(id);
        return responseConfig.success("ROLE_DELETE_BORROW", "borrow.delete.success", new Object[]{id}, null);
    }
    @PutMapping("{id}/return")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<Object>> returnBorrow(@PathVariable long id, HttpServletRequest request){
        return responseConfig.success("ROLE_UPDATE_BORROW", "borrow.update.success", null, service.returnBook(id));
    }
    @GetMapping("filter")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<BorrowResponse>>> filter(@AuthenticationPrincipal UserPrincipal up,
                                                                            HttpServletRequest request, @RequestParam(required = false) String user,
                                                                                   @RequestParam(required = false) String bookName,
                                                                                   @RequestParam(required = false) String bookCode,
                                                                                   @RequestParam(required = false)
                                                                                       @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate borrowDateMin,
                                                                                   @RequestParam(required = false)
                                                                                       @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate borrowDateMax,
                                                                                   @RequestParam(required = false)
                                                                                       @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate returnDateMin,
                                                                                   @RequestParam(required = false)
                                                                                       @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate returnDateMax,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "5") int size){
        V_User currentUser = up.getUser();
        return responseConfig.success("ROLE_VIEW_BORROW","borrow.filter.success",null, service.filter(currentUser ,user, bookName, bookCode, borrowDateMin, borrowDateMax, returnDateMin, returnDateMax, page, size));
    }
    @GetMapping("search")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<BorrowResponse>>> search(@AuthenticationPrincipal UserPrincipal up,
                                                                            HttpServletRequest request, @RequestBody BorrowFilterRequest filter,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "5") int size){
        V_User currentUser = up.getUser();
        return responseConfig.success("ROLE_VIEW_BORROW","borrow.filter.success", null, service.search(currentUser, filter, page, size));
    }

    }
