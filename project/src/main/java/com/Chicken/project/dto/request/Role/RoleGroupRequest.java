package com.Chicken.project.dto.request.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class RoleGroupRequest {
    @NotBlank(message = "{rolegroup.code.notEmpty}")
    private String roleGroupCode;

    @NotBlank(message = "{rolegroup.name.notEmpty}")
    private String roleGroupName;

    @NotBlank(message = "{rolegroup.description.notEmpty}")
    private String description;

    @NotEmpty(message = "{rolegroup.users.notEmpty}")
    private Set<Long> userIds;

    @NotEmpty(message = "{rolegroup.functions.notEmpty}")
    private Set<Long> functionIds;
}
