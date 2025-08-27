package com.Chicken.project.service;

import com.Chicken.project.dto.request.Comment.CommentCreateRequest;
import com.Chicken.project.dto.request.Comment.UpdateCommentRequest;
import com.Chicken.project.dto.response.Comment.CommentResponse;
import com.Chicken.project.dto.response.Comment.ShortCommentResponse;
import com.Chicken.project.entity.V_User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Service
public interface CommentService {
     List<ShortCommentResponse> viewComment(long articleId);
     CommentResponse getDetail(long articleId, long commentId);
     CommentResponse createComment(long articleId, CommentCreateRequest req, V_User currentUser);
     void deleteComment(long articleId,long commentId, V_User currentUser);
     CommentResponse updateComment(long articleId, long id, UpdateCommentRequest req, V_User currentUser);
    Page<CommentResponse> FILTER_ARTICLE(long articleId,
                                                @RequestParam(required = false) String author,
                                                @RequestParam(required = false) String content,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateTo,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createDateFrom,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size);
    @Transactional
    void softDeleteTree(long rootId);
}
