package com.Chicken.project.dto.request.User;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class UserFilterDto {
    private String username;
    private String email;
    private String phone;
}
