package com.Chicken.project.controller;


import com.Chicken.project.config.ResponseConfig;
import com.Chicken.project.dto.request.Comment.CommentCreateRequest;
import com.Chicken.project.dto.request.Comment.CommentFilterDto;
import com.Chicken.project.dto.request.Comment.UpdateCommentRequest;
import com.Chicken.project.dto.response.ApiResponse;
import com.Chicken.project.dto.response.Comment.CommentResponse;
import com.Chicken.project.dto.response.Comment.FullCommentResponse;
import com.Chicken.project.dto.response.Comment.ShortCommentResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.UserPrincipal;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.service.impl.CommentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/library/post/{articleId}/comment")
public class CommentController {
    private final ResponseConfig responseConfig;
    @Autowired
    private CommentServiceImpl service;
    @PostMapping("create")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<FullCommentResponse>> create(HttpServletRequest request, @PathVariable long articleId, @Valid @RequestBody CommentCreateRequest req, @AuthenticationPrincipal UserPrincipal up) {
        V_User currentUser = up.getUser();
        return responseConfig.success("ROLE_CREATE_COMMENT", "comment.create.success", new Object[]{articleId}, service.createComment(articleId, req, currentUser));
    }
    @PutMapping("{id}/update")
    @PreAuthorize("fileRole(#request) or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<FullCommentResponse>> update(HttpServletRequest request ,@PathVariable long articleId, @PathVariable long id,@RequestBody UpdateCommentRequest req, @AuthenticationPrincipal UserPrincipal up, @RequestParam(defaultValue = "2") int previewSize) {
        V_User currentUser = up.getUser();
        return responseConfig.success("ROLE_UPDATE_COMMENT", "comment.update.success", new Object[]{id}, service.updateComment(articleId, id, req, currentUser, previewSize));
    }
    @DeleteMapping("{id}/delete")
    @PreAuthorize("fileRole(#request) or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<CommentResponse>> delete(HttpServletRequest request ,@PathVariable long articleId, @PathVariable long id, @AuthenticationPrincipal UserPrincipal up) {
        V_User currentUser = up.getUser();
        service.deleteComment(articleId,id, currentUser);
        return responseConfig.success("ROLE_DELETE_COMMENT", "comment.delete.success", new Object[]{id}, null);
    }
    @GetMapping("")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<FullCommentResponse>>> view(HttpServletRequest request , @PathVariable long articleId, @RequestParam(defaultValue = "2") int previewSize, @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        return responseConfig.success("ROLE_VIEW_COMMENT", "comment.view.success", new Object[]{articleId}, service.viewComment(articleId, previewSize, page, size));
    }
    @GetMapping("{id}/detail")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<FullCommentResponse>> view(HttpServletRequest request, @PathVariable long articleId, @PathVariable long id, @RequestParam(defaultValue = "2") int previewSize) {
        return responseConfig.success("ROLE_VIEW_COMMENT", "comment.detail.success", null, service.getDetail(articleId, id, previewSize));
    }
    @GetMapping("filter")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<FullCommentResponse>>> view(HttpServletRequest request ,@PathVariable long articleId, @RequestParam(defaultValue = "2") int previewSize,
                                                                   @RequestParam(required = false) String author,
                                                                   @RequestParam(required = false) String content,
                                                                   @RequestParam(required = false)
                                                                       @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateTo,
                                                                   @RequestParam(required = false)
                                                                       @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateFrom,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        return responseConfig.success("ROLE_VIEW_COMMENT", "comment.filter.success", null, service.FILTER_COMMENT(articleId, previewSize, author, content,createDateTo, createDateFrom , page, size));
    }
    @GetMapping("search")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<FullCommentResponse>>> view(HttpServletRequest request ,@PathVariable long articleId, @RequestParam(defaultValue = "2") int previewSize,
                                                                               CommentFilterDto filter,
                                                                               @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        return responseConfig.success("ROLE_VIEW_COMMENT", "comment.filter.success", null, service.FILTER_COMMENT_DTO(articleId, previewSize, filter , page, size));
    }

}
