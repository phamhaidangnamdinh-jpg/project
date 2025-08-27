package com.Chicken.project.dto.response.Role;

import lombok.Data;

import java.util.List;

@Data
public class RoleGroupResponse {
    private String roleGroupCode;
    private String roleGroupName;
    private String description;
    private List<String> userName;
    private List<String> functionCode;
}
