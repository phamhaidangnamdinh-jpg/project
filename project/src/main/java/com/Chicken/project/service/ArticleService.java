package com.Chicken.project.service;

import com.Chicken.project.dto.request.Article.ArticleRequest;
import com.Chicken.project.dto.request.Article.UpdateArticleRequest;
import com.Chicken.project.dto.response.Article.ArticleResponse;
import com.Chicken.project.dto.response.Article.ShortArticleResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.V_User;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public interface ArticleService {
    public ArticleResponse createArticle(ArticleRequest req, V_User currentUser);
    public PageResponse<ShortArticleResponse> getArticle(int page, int size);
    public void deleteArticle(long id, V_User currentUser);
    public ArticleResponse getArticleById(long id);
    public ArticleResponse updateArticle(long id, UpdateArticleRequest req, V_User currentUser);

    public PageResponse<ArticleResponse> FILTER_ARTICLE(@RequestParam(required = false) String title,
                                                        @RequestParam(required = false) String author,
                                                        @RequestParam(required = false) String bookName,
                                                        @RequestParam(required = false) String content,
                                                        @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateTo,
                                                        @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateFrom,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size);
//    public ArticleResponse likePost(long articleId);
//    public ArticleResponse dislikePost(long articleId);
}
