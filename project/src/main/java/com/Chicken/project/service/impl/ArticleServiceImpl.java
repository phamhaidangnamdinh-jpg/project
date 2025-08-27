package com.Chicken.project.service.impl;

import com.Chicken.project.dto.request.Article.ArticleFilterDTO;
import com.Chicken.project.dto.request.Article.ArticleRequest;
import com.Chicken.project.dto.request.Article.UpdateArticleRequest;
import com.Chicken.project.dto.response.Article.ArticleResponse;
import com.Chicken.project.dto.response.Article.ShortArticleResponse;
import com.Chicken.project.dto.response.Comment.CommentResponse;
import com.Chicken.project.dto.response.Article.TopLikedArticleResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.Article;
import com.Chicken.project.entity.Comment;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.exception.BusinessException;
import com.Chicken.project.repository.ArticleRepo;
import com.Chicken.project.repository.BookRepository;
import com.Chicken.project.repository.CommentRepo;
import com.Chicken.project.repository.UserRepo;
import com.Chicken.project.service.ArticleService;
import com.Chicken.project.utils.PageResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    ArticleRepo repo;
    @Autowired
    CommentRepo CRepo;
    @Autowired
    UserRepo uRepo;
    @Autowired
    BookRepository bRepo;
    @Autowired
    UserArticleInteractionServiceImpl userService;
    private static final Logger log =  LoggerFactory.getLogger(BookServiceImpl.class);
    public static ShortArticleResponse toShortResponse(Article article){
        log.info("Converting article id '{}' into short response", article.getId());
        ShortArticleResponse res = new ShortArticleResponse();
        res.setTitle(article.getTitle());
        res.setDislikeCount(article.getDisLikeCount().get());
        res.setLikeCount(article.getLikeCount().get());
        return res;
    }

    public static ArticleResponse toResponse(Article article) {
        log.info("Converting article id '{}' into response", article.getId());
        ArticleResponse res = new ArticleResponse();
        res.setAuthorName(article.getAuthor().getUsername());
        res.setTitle(article.getTitle());
        res.setBookName(article.getBook().getTitle());
        res.setContent(article.getContent());
        res.setCreateDate(article.getCreateDate());
        res.setUpdateDate(article.getUpdateDate());
        List<Comment> com =article.getComments();
        List<CommentResponse> cres = Optional.ofNullable(article.getComments())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(comm -> {
                    CommentResponse c = new CommentResponse();
                    c.setContent(comm.getContent());
                    c.setAuthorName(comm.getAuthor().getUsername());
                    c.setArticleName(article.getTitle());
                    return c;
                })
                .toList();
        res.setComments(cres);
        res.setDislikeCount(article.getDisLikeCount().get());
        res.setLikeCount(article.getLikeCount().get());
        return res;
    }
    public ArticleResponse createArticle(ArticleRequest req, V_User currentUser) {
        log.info("Received request to create new article");
        Article art = new Article();
        art.setTitle(req.getTitle());
        art.setContent(req.getContent());
        art.setAuthor(currentUser);
        art.setCreateDate(LocalDate.now());
        art.setUpdateDate(null);
        if(!bRepo.existsByCode(req.getBookCode())){
            log.warn("Book code '{}' doesn't exist", req.getBookCode());
            throw new BusinessException("error.book.notFound");
        }
        art.setBook(bRepo.findByCode(req.getBookCode()));
        Article article = repo.save(art);
        log.info("Created new article with id '{}'", article.getId());
        return toResponse(art);
    }
    public PageResponse<ShortArticleResponse> getArticle(int page, int size) {
        log.info("Viewing article list page '{}'", page);

        Pageable pageable = PageRequest.of(page, size);
        return PageResponseUtil.fromPage(repo.findAll(pageable)
                .map(ArticleServiceImpl::toShortResponse));
    }
    public void deleteArticle(long id, V_User currentUser) {
        log.info("Received request to delete article id '{}'", id);
        if(!repo.existsById(id)) {
            log.info("Article with id '{}' doesn't exist", id);
            throw new BusinessException("error.article.notFound");
        }
        Article art = repo.findById(id).orElseThrow(() -> new BusinessException("error.article.notFound"));

        List<Comment> comments = art.getComments();
        comments.forEach(c -> { c.setDeleted(true); CRepo.save(c); });
        art.setDeleted(true);
        repo.save(art);
        log.info("Deleted article with id '{}'", id);
    }
    public ArticleResponse getArticleById(long id) {
        log.info("Received request to view article with id '{}'", id);
        if(!repo.existsById(id)) {
            log.warn("Article with id '{}' doesn't exist", id);
            throw new BusinessException("error.article.notFound");
        }
        log.info("Viewing Article with id '{}'", id);
        return repo.findById(id).map(ArticleServiceImpl::toResponse).orElse(null);
    }
    public ArticleResponse updateArticle(long id, UpdateArticleRequest req, V_User currentUser) {
        log.info("Received request to update article with id '{}'", id);
        if(!repo.existsById(id)) {
            log.warn("Article with id '{}' doesn't exist", id);
            throw new BusinessException("error.article.notFound");
        }
        log.info("Updating article with id '{}'", id);
        Article article = repo.findById(id).get();
//        boolean isOwner = (article.getAuthor().getId() == currentUser.getId());
//        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
//        if(!isAdmin && !isOwner) {
//            log.warn("No permission to update article id '{}'", id);
//            throw new BusinessException("error.article.noPermission");
//        }
        String title = req.getTitle();
        if(title!=null) article.setTitle(title);
        String content = req.getContent();
        if(content!=null) article.setContent(content);
        article.setUpdateDate(LocalDate.now());
        return toResponse(repo.save(article));
    }

    public PageResponse<ArticleResponse> FILTER_ARTICLE(@RequestParam(required = false) String title,
                                             @RequestParam(required = false) String author,
                                             @RequestParam(required = false) String bookName,
                                             @RequestParam(required = false) String content,
                                             @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateTo,
                                             @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateFrom,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to filter article");
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> result = repo.filterArticle(title, author, bookName, content, createDateFrom, createDateTo, pageable);
        log.info("Filtering articles");
        return PageResponseUtil.fromPage(result.map(ArticleServiceImpl::toResponse));
    }
    public PageResponse<ArticleResponse> FILTER_ARTICLE_DTO(
            @RequestBody ArticleFilterDTO filter, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to filter article");
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> result = repo.filterArticle(filter.getTitle(), filter.getAuthor(), filter.getBookName(),
                filter.getContent(), filter.getCreateDateFrom(), filter.getCreateDateTo(), pageable);
        log.info("Filtering articles");
        return PageResponseUtil.fromPage(result.map(ArticleServiceImpl::toResponse));
    }

    public ArticleResponse likePost(long articleId){
        log.info("Received request to like post id '{}'", articleId);
        if(!repo.existsById(articleId)){
            log.warn("Article id '{}' does no exist", articleId);
            throw new BusinessException("error.article.notFound");
        }
        Article article = repo.findById(articleId).get();
        if(userService.isLikedPost(articleId)){
            article.decrementLike();
            log.info("Unliked post, current like: '{}' dislike: '{}'", article.getLikeCount(), article.getDisLikeCount());
            userService.removeFromLikedPosts(articleId);
        } else if (userService.isDislikedPost(articleId)) {
            article.decrementDislike();
            article.incrementLike();
            log.info("Dislike to like, current like: '{}' dislike: '{}'", article.getLikeCount(), article.getDisLikeCount());
            userService.removeFromDislikedPosts(articleId);
            userService.addToLikedPosts(articleId);
        } else{
            article.incrementLike();
            log.info("Liked post, current like: '{}' dislike: '{}'", article.getLikeCount(), article.getDisLikeCount());
            userService.addToLikedPosts(articleId);
        }
        return toResponse(repo.save(article));
    }
    public ArticleResponse dislikePost(long articleId){
        log.info("Received request to like post id '{}'", articleId);
        if(!repo.existsById(articleId)){
            log.warn("Article id '{}' does no exist", articleId);
            throw new BusinessException("error.article.notFound");
        }
        Article article = repo.findById(articleId).get();
        if(userService.isDislikedPost(articleId)){
            article.decrementDislike();
            log.info("Un-disliked post, current like: '{}' dislike: '{}'", article.getLikeCount(), article.getDisLikeCount());
            userService.removeFromDislikedPosts(articleId);
        } else if (userService.isLikedPost(articleId)) {
            article.incrementDislike();
            article.decrementLike();
            log.info("Like to dislike, current like: '{}' dislike: '{}'", article.getLikeCount(), article.getDisLikeCount());
            userService.removeFromLikedPosts(articleId);
            userService.addToDislikedPosts(articleId);
        } else{
            article.incrementDislike();
            log.info("Disliked post, current like: '{}' dislike: '{}'", article.getLikeCount(), article.getDisLikeCount());
            userService.addToDislikedPosts(articleId);
        }
        return toResponse(repo.save(article));
    }
    public List<TopLikedArticleResponse> top5Article(){
        log.info("Getting 5 most liked posts");
        return repo.findTop5ByOrderByLikeCountDesc().stream().map(a -> new TopLikedArticleResponse(a.getTitle(), a.getAuthor().getUsername(), a.getLikeCount().get())).collect(Collectors.toList());
    }
}
