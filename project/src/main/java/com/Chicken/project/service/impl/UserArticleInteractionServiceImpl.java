package com.Chicken.project.service.impl;

import com.Chicken.project.entity.UserPrincipal;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.repository.RoleGroupRepo;
import com.Chicken.project.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserArticleInteractionServiceImpl {
    @Autowired
    private UserRepo repo;
    private static final Logger log =  LoggerFactory.getLogger(UserArticleInteractionServiceImpl.class);
    private V_User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getUser();
        }
        throw new RuntimeException("No authenticated user found");
    }

    public void addToLikedPosts(long articleId){
        V_User currentUser = getCurrentUser();
        currentUser.addToLikedPosts(articleId);
        log.info("Added article id {} to user {} liked posts", articleId, currentUser.getUsername());
        repo.save(currentUser);
    }

    public void removeFromLikedPosts(long articleId){
        V_User currentUser = getCurrentUser();
        currentUser.removeFromLikedPosts(articleId);
        log.info("removed article id {} from user {} liked posts", articleId, currentUser.getUsername());
        repo.save(currentUser);
    }

    public void addToDislikedPosts(long articleId){
        V_User currentUser = getCurrentUser();
        currentUser.addToDislikedPosts(articleId);
        log.info("Added article id {} to user {} disliked posts", articleId, currentUser.getUsername());
        repo.save(currentUser);
    }

    public void removeFromDislikedPosts(long articleId){
        V_User currentUser = getCurrentUser();
        currentUser.removeFromDislikedPosts(articleId);
        log.info("Removed article id {} from user {} disliked posts", articleId, currentUser.getUsername());
        repo.save(currentUser);
    }

    public boolean isLikedPost(long articleId){
        V_User currentUser = getCurrentUser();
        System.out.println(currentUser.getLikedPosts());
//        System.out.println(currentUser.getUsername());
        return currentUser.getLikedPosts().stream().anyMatch(likedPost -> likedPost == articleId);
    }
    public boolean isDislikedPost(long articleId){
        V_User currentUser = getCurrentUser();
        return currentUser.getDisLikedPosts().stream().anyMatch(likedPost -> likedPost == articleId);
    }
}
