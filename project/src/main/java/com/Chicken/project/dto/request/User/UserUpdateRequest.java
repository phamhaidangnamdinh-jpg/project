package com.Chicken.project.dto.request.User;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;
@Data
public class UserUpdateRequest {
    private String username;

    private String password;
    private String fullname;
    @Email(message = "{error.email.invalid}")
    private String email;
    private String phone;
    private String address;
    private String identityNumber;
    private Integer age;
    private LocalDate birthday;
}
