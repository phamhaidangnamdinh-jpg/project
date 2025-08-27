package com.Chicken.project.service;

import com.Chicken.project.dto.request.Role.RoleGroupRequest;
import com.Chicken.project.dto.request.Role.RoleGroupUpdateRequest;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.dto.response.Role.RoleGroupResponse;
import com.Chicken.project.dto.response.Role.ShortRoleGroupResponse;
import com.Chicken.project.entity.Function;
import com.Chicken.project.entity.RoleGroup;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.exception.BusinessException;
import com.Chicken.project.utils.PageResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface RoleGroupService {
    PageResponse<ShortRoleGroupResponse> getAll(int page, int size);
    RoleGroupResponse getById(Long id);
    RoleGroupResponse create(RoleGroupRequest req);
    RoleGroupResponse update(Long id, RoleGroupUpdateRequest req);
    Boolean delete(Long id);

    PageResponse<RoleGroupResponse> FILTER_ROLE(@RequestParam(required = false) String roleGroupCode,
                                                       @RequestParam(required = false) String roleGroupName,
                                                       @RequestParam(required = false) String description,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size);
}
