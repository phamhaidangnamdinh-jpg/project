package com.Chicken.project.controller;

import com.Chicken.project.config.ResponseConfig;
import com.Chicken.project.dto.request.Article.ArticleFilterDTO;
import com.Chicken.project.dto.request.Article.ArticleRequest;
import com.Chicken.project.dto.request.Article.UpdateArticleRequest;
import com.Chicken.project.dto.response.ApiResponse;
import com.Chicken.project.dto.response.Article.ArticleResponse;
import com.Chicken.project.dto.response.Article.TopLikedArticleResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.UserPrincipal;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.service.impl.ArticleServiceImpl;
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
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library/post")
public class ArticleController {
    @Autowired
    private ArticleServiceImpl service;
    private final ResponseConfig responseConfig;
    @GetMapping("")
//    @PreAuthorize("hasRole('VIEW_POST')")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<?>>> getArticles(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size, Locale locale) {
        System.out.println("✅ fileRole invoked with URI = " + request.getRequestURI());
        return responseConfig.success("ROLE_VIEW_POST", "post.view.success",null, service.getArticle(page, size));
    }
    @PostMapping("create")
//    @PreAuthorize("hasRole('CREATE_POST')")
    @PreAuthorize("fileRole(#req)")
    public ResponseEntity<ApiResponse<ArticleResponse>> createArticles(HttpServletRequest req, @Valid @RequestBody ArticleRequest request, @AuthenticationPrincipal UserPrincipal up) {
        V_User currentUser = up.getUser();
        return responseConfig.success("ROLE_CREATE_POST", "post.create.success", null, service.createArticle(request, currentUser));
    }
    @DeleteMapping("{id}/delete")
//    @PreAuthorize("hasRole('DELETE_POST')")
    @PreAuthorize("fileRole(#request) or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<String>> deleteArticles(HttpServletRequest request, @PathVariable Integer id, @AuthenticationPrincipal UserPrincipal up) {
        V_User user = up.getUser();
        service.deleteArticle(id, user);
        return responseConfig.success("ROLE_DELETE_POST","post.delete.success",new Object[]{id}, null);
    }
    @GetMapping("{id}/detail")
//    @PreAuthorize("hasRole('VIEW_POST')")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticle(HttpServletRequest request, @PathVariable long id) {
        return responseConfig.success("ROLE_VIEW_POST", "post.detail.success",new Object[]{id}, service.getArticleById(id));
    }
    @PutMapping("{id}/update")
//    @PreAuthorize("hasRole('UPDATE_POST')")
    @PreAuthorize("fileRole(#req) or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<ArticleResponse>> updateArticle(HttpServletRequest req, @PathVariable long id, @RequestBody UpdateArticleRequest request, @AuthenticationPrincipal UserPrincipal up) {
        V_User currentUser = up.getUser();
        return responseConfig.success("ROLE_UPDATE_POST", "post.update.success",new Object[]{id}, service.updateArticle(id, request, currentUser));
    }
    @GetMapping("filter")
//    @PreAuthorize("hasRole('VIEW_POST')")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<ArticleResponse>>> filterArticles(
            HttpServletRequest request, @RequestParam(required = false) String title,
                                                                             @RequestParam(required = false) String author,
                                                                             @RequestParam(required = false) String bookName,
                                                                             @RequestParam(required = false) String content,
                                                                                @RequestParam(required = false)
                                                                                @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateTo,
                                                                                @RequestParam(required = false)
                                                                                @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateFrom,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        return responseConfig.success("ROLE_VIEW_POST", "post.filter.success", null, service.FILTER_ARTICLE(title, author, bookName, content, createDateTo, createDateFrom, page, size));
    }
    @GetMapping("search")
//    @PreAuthorize("hasRole('VIEW_POST')")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<ArticleResponse>>> filterArticlesWithDto(
            HttpServletRequest request, @RequestBody ArticleFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return responseConfig.success("ROLE_VIEW_POST", "post.filter.success", null, service.FILTER_ARTICLE_DTO(filter, page, size));
    }
    @PostMapping("{id}/like")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<ArticleResponse>> likePost(@PathVariable long id, HttpServletRequest request){
        return responseConfig.success("ROLE_LIKE_POST", "post.like.success", new Object[]{id}, service.likePost(id));
    }
    @PostMapping("{id}/dislike")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<ArticleResponse>> dislikePost(@PathVariable long id, HttpServletRequest request){
        return responseConfig.success("ROLE_LIKE_POST", "post.dislike.success", new Object[]{id}, service.dislikePost(id));
    }
    @GetMapping("top-liked")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<List<TopLikedArticleResponse>>> top5LikedPosts(HttpServletRequest request){
        return responseConfig.success("ROLE_LIKE_POST", "post.top-liked.success", null, service.top5Article());
    }
}
