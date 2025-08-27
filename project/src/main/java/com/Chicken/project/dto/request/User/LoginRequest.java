package com.Chicken.project.dto.request.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username must not be empty")
    private String username;
    @NotBlank(message = "Password must not be empty")
    private String password;
}
