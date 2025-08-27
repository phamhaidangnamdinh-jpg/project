package com.Chicken.project.dto.request.Role;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class RoleFilterDto {
    private String roleGroupCode;
    private String roleGroupName;
    private String description;
}
