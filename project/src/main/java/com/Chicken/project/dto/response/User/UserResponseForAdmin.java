package com.Chicken.project.dto.response.User;

import com.Chicken.project.dto.response.Article.ArticleResponse;
import com.Chicken.project.dto.response.Borrow.BorrowResponse;
import com.Chicken.project.dto.response.Comment.CommentResponse;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserResponseForAdmin {
    private Long id;
    private String username;
    private String fullname;
    private String email;
    private String phone;
    private String address;
    private String identityNumber;
    private int age;
    private LocalDate birthday;
    private List<CommentResponse> comments;
    private List<ArticleResponse> articles;
    private String roleGroupName;
    private List<BorrowResponse> borrows;
    private List<Long> dislikePostIds;
    private List<Long> likePostIds;
}
