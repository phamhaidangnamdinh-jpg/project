package com.Chicken.project.dto.request.Role;

import lombok.Data;

import java.util.List;

@Data
public class UserToAddRole {
    private List<Long> id;
    private String roleCode;
}
