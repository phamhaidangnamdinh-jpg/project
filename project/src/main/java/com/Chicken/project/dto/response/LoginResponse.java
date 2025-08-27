package com.Chicken.project.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private UserLogin user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin{
        private long id;
        private String email;
        private String name;
    }
}
