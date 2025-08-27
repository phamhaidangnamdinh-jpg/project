package com.Chicken.project.dto.response.User;

import lombok.Data;

@Data
public class UserShortResponse {
    private Long id;
    private String username;
    private String fullname;
}