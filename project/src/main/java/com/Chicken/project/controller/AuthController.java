package com.Chicken.project.controller;

import com.Chicken.project.config.ResponseConfig;
import com.Chicken.project.dto.request.User.LoginRequest;
import com.Chicken.project.dto.response.ApiResponse;
import com.Chicken.project.dto.response.LoginResponse;
import com.Chicken.project.entity.UserPrincipal;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.service.impl.AuthService;
import com.Chicken.project.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library/auth")
public class AuthController {
    @Autowired
    private UserServiceImpl service;
    @Autowired
    private AuthService authService;
    private final ResponseConfig responseConfig;
    @PostMapping("login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.verify(request, response);
        if(loginResponse == null){
            return responseConfig.unauthorized("LOGIN", "auth.login.failure");
        }
        else return responseConfig.success("ROLE_LOGIN_USER", "auth.login.success", null , loginResponse);
    }
    @PostMapping("refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                                              HttpServletResponse response){
        LoginResponse loginResponse = authService.refresh(refreshToken, response);
        if(loginResponse == null){
            return responseConfig.unauthorized("REFRESH", "auth.refresh.failure");
        }
        else return responseConfig.success("REFRESH", "auth.refresh.success",null, loginResponse);
    }
    @PostMapping("logout")
    public ResponseEntity<ApiResponse<Objects>> logout(HttpServletResponse response, @AuthenticationPrincipal UserPrincipal up){
        V_User user = up.getUser();
        authService.logout(response, user);
        return responseConfig.success("LOGOUT", "auth.logout.success", null, null);
    }
}
