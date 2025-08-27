package com.Chicken.project.dto.request.Role;

import lombok.Data;

import java.util.Set;

@Data
public class RoleGroupUpdateRequest {
    private String roleGroupCode;
    private String roleGroupName;
    private String description;
    private Set<Long> userIds;
    private Set<Long> functionIds;
}
