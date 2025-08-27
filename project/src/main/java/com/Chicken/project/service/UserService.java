package com.Chicken.project.service;

import com.Chicken.project.dto.request.User.LoginRequest;
import com.Chicken.project.dto.request.User.UserRequest;
import com.Chicken.project.dto.request.User.UserUpdateRequest;
import com.Chicken.project.dto.response.LoginResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.dto.response.User.UserResponseForAdmin;
import com.Chicken.project.dto.response.User.UserShortResponse;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface UserService {
    UserResponseForAdmin createUser(UserRequest userRequest);
    PageResponse<UserShortResponse> viewUser(int page, int size);
    UserResponseForAdmin viewUserDetail(Long id);
    UserResponseForAdmin updateUser(long id, UserUpdateRequest userRequest);
    List<UserShortResponse> search(String keyword);
    boolean deleteUser(long id);
    PageResponse<UserShortResponse> filterUser(@RequestParam(required = false) String username,
                                                @RequestParam(required = false) String email,
                                                @RequestParam(required = false) String phone,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size);
    UserResponseForAdmin register(UserRequest request);
    LoginResponse verify(LoginRequest request);
    ByteArrayInputStream exportToCsv() throws IOException;


}
