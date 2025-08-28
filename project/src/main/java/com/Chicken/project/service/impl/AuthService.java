package com.Chicken.project.service.impl;

import com.Chicken.project.dto.request.User.LoginRequest;
import com.Chicken.project.dto.response.LoginResponse;
import com.Chicken.project.entity.UserPrincipal;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.repository.RoleGroupRepo;
import com.Chicken.project.repository.UserRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    private UserRepo repo;
    @Autowired
    private JWTService jwtService;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    private RoleGroupRepo rRepo;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private V_User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getUser();
        }
        throw new RuntimeException("No authenticated user found");
    }

    public LoginResponse verify(LoginRequest request, HttpServletResponse response) {
        log.info("Login attempt for user '{}'", request.getUsername());
        Authentication auth;
        try {
            auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user '{}': {}", request.getUsername(), e.getMessage());
            return null;
        }
        String accessToken = jwtService.generateAccessToken(request.getUsername());
        SecurityContextHolder.getContext().setAuthentication(auth);
        V_User currentUser = getCurrentUser();

        String refreshToken = jwtService.generateRefreshToken(currentUser.getUsername(), currentUser.getEmail());
        currentUser.setRefreshToken(refreshToken);
        repo.save(currentUser);

        Cookie cookie = new jakarta.servlet.http.Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(currentUser.getId(), currentUser.getUsername(), currentUser.getEmail());
        LoginResponse loginResponse = new LoginResponse(accessToken, userLogin);
        log.info("User '{}' logged in successfully", currentUser.getUsername());
        return loginResponse;
    }
    public LoginResponse refresh(String refreshToken, HttpServletResponse response){
        log.info("Attempting to refresh token");
        String username = jwtService.extractUserName(refreshToken);
        V_User user = repo.findByUsername(username);
        if(!jwtService.isValid(refreshToken, user)){
            log.warn("Refresh token is invalid or expired for user '{}'", username);
            return null;
        }
        String newAccessToken = jwtService.generateAccessToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username, user.getEmail());
        user.setRefreshToken(newRefreshToken);
        repo.save(user);
        Cookie cookie = new Cookie("refreshToken", newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60 * 60);
        response.addCookie(cookie);

        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(user.getId(), user.getUsername(), user.getEmail());
        LoginResponse loginResponse = new LoginResponse(newAccessToken, userLogin);
        log.info("Refresh token successful for user '{}'", username);
        return loginResponse;
    }
    public void logout(HttpServletResponse response, V_User user){
        log.info("User '{}' is logging out", user.getUsername());

        user.setRefreshToken(null);
        repo.save(user);
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        log.info("User '{}' logged out and refresh token cleared", user.getUsername());
    }
}
