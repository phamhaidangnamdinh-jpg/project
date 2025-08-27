package com.Chicken.project.service.impl;

import com.Chicken.project.dto.request.Comment.CommentCreateRequest;
import com.Chicken.project.dto.request.Comment.CommentFilterDto;
import com.Chicken.project.dto.request.Comment.UpdateCommentRequest;
import com.Chicken.project.dto.response.Article.ShortArticleResponse;
import com.Chicken.project.dto.response.Comment.CommentResponse;
import com.Chicken.project.dto.response.Comment.FullCommentResponse;
import com.Chicken.project.dto.response.Comment.ShortCommentResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.Article;
import com.Chicken.project.entity.Comment;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.exception.BusinessException;
import com.Chicken.project.repository.ArticleRepo;
import com.Chicken.project.repository.CommentRepo;
import com.Chicken.project.repository.UserRepo;
import com.Chicken.project.service.CommentService;
import com.Chicken.project.utils.PageResponseUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl {
    @Autowired
    CommentRepo repo;
    @Autowired
    ArticleRepo aRepo;
    @Autowired
    UserRepo uRepo;
    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);
    public static ShortCommentResponse toShortResponse(Comment comment){
        log.info("Converting comment into short responses");
        ShortCommentResponse res = new ShortCommentResponse();
        res.setAuthorName(comment.getAuthor().getUsername());
        res.setContent(comment.getContent());
        return res;
    }

    public static CommentResponse toResponse(Comment comment) {
        log.info("Converting comment into responses");
        CommentResponse res = new CommentResponse();
        res.setArticleName(comment.getArticle().getTitle());
        res.setAuthorName(comment.getAuthor().getUsername());
        res.setContent(comment.getContent());
        res.setCreateDate(comment.getCreateDate());
        res.setUpdateDate(comment.getUpdateDate());
        return res;
    }

    public static FullCommentResponse toFullResponse(Comment comment, int previewSize, CommentRepo repo) {
        log.info("Converting comment into responses");
        FullCommentResponse res = new FullCommentResponse();
        res.setArticleName(comment.getArticle().getTitle());
        res.setAuthorName(comment.getAuthor().getUsername());
        res.setContent(comment.getContent());
        res.setCreateDate(comment.getCreateDate());
        res.setUpdateDate(comment.getUpdateDate());

        Pageable previewPage = PageRequest.of(0, previewSize, Sort.by("createDate").ascending());
        List<Comment> previewReplies = repo.findByParent(comment, previewPage).getContent();
        res.setReplies(previewReplies.stream().map(CommentServiceImpl::toShortResponse).toList());

        long totalReplies = repo.countByParent(comment);
        res.setTotalReplies(totalReplies);
        return res;
    }
    public PageResponse<FullCommentResponse> viewComment(long articleId, int previewSize, int page, int size) {
        log.info("Received request to view top comments on article id '{}'", articleId);
        if(!aRepo.existsById(articleId)) {
            log.warn("Article with id '{}' does not exist", articleId);
            throw new BusinessException("error.article.notFound");
        }
        Pageable pageable = PageRequest.of(page, size);

        Article article = aRepo.findById(articleId).get();
//        List<Comment> comments = Optional.ofNullable(article.getComments()).orElse(new ArrayList<>());
        Page<Comment> rootComments = repo.findByArticleAndParentIsNull(article, pageable);
        log.info("Viewing top-level comments on article id '{}'", articleId);
        return PageResponseUtil.fromPage(rootComments.map(c -> toFullResponse(c, previewSize, repo)));
    }
    public FullCommentResponse getDetail(long articleId, long commentId, int previewSize){
        log.info("Received request to view details of comment id '{}'", commentId);
        if(!aRepo.existsById(articleId)) {
            log.warn("Article with id '{}' does not exist", articleId);
            throw new BusinessException("error.article.notFound");
        }
        if (!repo.existsById(commentId)) {
            log.warn("Comment with id '{}' does not exist", commentId);
            throw new BusinessException("error.comment.notFound");
        }
        Comment comment = repo.findById(commentId).get();
        if(comment.getArticle().getId()!=articleId){
            log.warn("Comment with id '{}' does not belong to article id '{}'", commentId, articleId);
            throw new BusinessException("error.comment.articleMismatch");
        }
        log.info("Viewing details of comment id '{}'", commentId);
        return  toFullResponse(comment, previewSize, repo);

    }
        public FullCommentResponse createComment(long articleId, CommentCreateRequest req, V_User currentUser) {
            log.info("Received request create comment on article with id '{}'", articleId);
            if(!aRepo.existsById(articleId)){
                log.warn("Article with id '{}' does not exist", articleId);
                throw new BusinessException("error.article.notFound");
            }
            Article article = aRepo.findById(articleId).get();

            Comment comment = new Comment();
            comment.setArticle(article);
            comment.setAuthor(currentUser);
            comment.setCreateDate(LocalDate.now());
            comment.setContent(req.getContent());
            if (req.getParentId() != null) {
                Comment parent = repo.findById(req.getParentId())
                        .orElseThrow(() -> new BusinessException("error.comment.notFound"));
                comment.setParent(parent);
                comment.setArticle(parent.getArticle());
            } else {
                comment.setArticle(article);
            }
            return toFullResponse(repo.save(comment), 2, repo);
    }

    @Transactional
    public void softDeleteTree(long rootId) {
        List<Comment> comments = repo.findCommentTree(rootId);
        comments.forEach(c-> c.setDeleted(true));
        repo.saveAll(comments);
    }

    public void deleteComment(long articleId,long commentId, V_User currentUser) {
        log.info("Received request to delete comment id '{}'", commentId);
        if (!repo.existsById(commentId)) {
            log.warn("Comment with id '{}' does not exist", commentId);
            throw new BusinessException("error.comment.notFound");
        }
        Comment comment = repo.findById(commentId).get();
        if(comment.getArticle().getId()!=articleId){
            log.warn("Comment with id '{}' does not belong to article id '{}'", commentId, articleId);
            throw new BusinessException("error.comment.articleMismatch");
        }
//        boolean isOwner = (comment.getAuthor().getId() == currentUser.getId());
//        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
//        if(!isAdmin && !isOwner) {
//            log.warn("No permission to delete this comment");
//            throw new BusinessException("error.comment.noPermission");
//        }
        softDeleteTree(commentId);
//        comment.setDeleted(true);
        log.info("Deleted comment id '{}'", commentId);
        repo.save(comment);
    }
    public FullCommentResponse updateComment(long articleId, long id, UpdateCommentRequest req, V_User currentUser, int previewSize) {
        log.info("Received request to update comment id '{}'", id);
        if (!repo.existsById(id)) {
            log.warn("Comment with id '{}' does not exist", id);
            throw new BusinessException("error.comment.notFound");
        }
        Comment com = repo.findById(id).get();
        if(com.getArticle().getId()!=articleId){
            log.warn("Comment with id '{}' does not belong to article id '{}'", id, articleId);
            throw new BusinessException("error.comment.articleMismatch");
        }
//        boolean isOwner = (com.getAuthor().getId() == currentUser.getId());
//        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
//        if(!isAdmin && !isOwner) {
//            log.warn("No permission to update this comment");
//            throw new BusinessException("error.comment.noPermission");
//        }
        String content = req.getContent();
        if(content!=null) com.setContent(content);
        if(content!= null) com.setUpdateDate(LocalDate.now());
        log.info("Updated comment");
        return toFullResponse(repo.save(com), previewSize, repo);
    }
    public PageResponse<FullCommentResponse> FILTER_COMMENT(long articleId, int previewSize,
                                                @RequestParam(required = false) String author,
                                                @RequestParam(required = false) String content,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateTo,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateFrom,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to view filtered comments");
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> result = repo.filterComment(articleId ,author, content,createDateFrom, createDateTo, pageable);
        log.info("Showing filtered comments, total comments '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(c-> toFullResponse(c, previewSize, repo)));
    }
    public PageResponse<FullCommentResponse> FILTER_COMMENT_DTO(long articleId, int previewSize,
                                                    @RequestBody CommentFilterDto filter,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to view filtered comments");
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> result = repo.filterComment(articleId ,filter.getAuthor(), filter.getContent(),filter.getCreateDateFrom(), filter.getCreateDateTo(), pageable);
        log.info("Showing filtered comments, total comments '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(c-> toFullResponse(c, previewSize, repo)));
    }
}
