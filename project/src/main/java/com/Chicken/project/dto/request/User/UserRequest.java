package com.Chicken.project.dto.request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

import java.time.LocalDate;

@Data
public class UserRequest {
    @NotBlank(message = "{error.username.notBlank}")
    private String username;

    @NotBlank(message = "{error.password.notBlank}")
    private String password;

    @NotBlank(message = "{error.fullname.notBlank}")
    private String fullname;

    @Email(message = "{error.email.invalid}")
    @NotBlank(message = "{error.email.notBlank}")
    private String email;

    @NotBlank(message = "{error.phone.notBlank}")
    private String phone;

    @NotBlank(message = "{error.address.notBlank}")
    private String address;

    @NotBlank(message = "{error.identityNumber.notBlank}")
    private String identityNumber;

    @NotNull(message = "{error.age.notNull}")
    private Integer age;

    @NotNull(message = "{error.birthday.notNull}")
    private LocalDate birthday;
}
