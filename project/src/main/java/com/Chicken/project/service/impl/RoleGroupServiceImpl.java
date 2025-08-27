package com.Chicken.project.service.impl;

import com.Chicken.project.dto.request.Role.RoleFilterDto;
import com.Chicken.project.dto.request.Role.RoleGroupRequest;
import com.Chicken.project.dto.request.Role.RoleGroupUpdateRequest;
import com.Chicken.project.dto.request.Role.UserToAddRole;
import com.Chicken.project.dto.response.Article.ArticleResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.dto.response.Role.RoleGroupResponse;
import com.Chicken.project.dto.response.Role.ShortRoleGroupResponse;
import com.Chicken.project.entity.Article;
import com.Chicken.project.entity.Function;
import com.Chicken.project.entity.RoleGroup;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.exception.BusinessException;
import com.Chicken.project.repository.FunctionRepo;
import com.Chicken.project.repository.RoleGroupRepo;
import com.Chicken.project.repository.UserRepo;
import com.Chicken.project.service.RoleGroupService;
import com.Chicken.project.utils.PageResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleGroupServiceImpl implements RoleGroupService {
    private static final Logger log = LoggerFactory.getLogger(JWTService.class);
    @Autowired
    private RoleGroupRepo repo;
    @Autowired
    private UserRepo uRepo;
    @Autowired
    private FunctionRepo fRepo;

    private ShortRoleGroupResponse toShortResponse(RoleGroup role) {
        log.info("Converting role into short response");
        ShortRoleGroupResponse rp = new ShortRoleGroupResponse();
        rp.setRoleGroupCode(role.getRoleGroupCode());
        rp.setRoleGroupName(role.getRoleGroupName());
        return rp;
    }

    private RoleGroupResponse toResponse(RoleGroup role) {
        log.info("Converting role into response");
        RoleGroupResponse rp = new RoleGroupResponse();
        rp.setRoleGroupCode(role.getRoleGroupCode());
        rp.setRoleGroupName(role.getRoleGroupName());
        rp.setUserName(role.getUsers().stream().map(V_User::getUsername).collect(Collectors.toList()));
        rp.setFunctionCode(role.getFunctions().stream().map(Function::getFunctionCode).collect(Collectors.toList()));
        return rp;
    }

    @Override
    public PageResponse<ShortRoleGroupResponse> getAll(int page, int size) {
        log.info("Viewing all roles: page '{}'", page);
        Pageable pageable = PageRequest.of(page, size);
        return PageResponseUtil.fromPage(repo.findAll(pageable)
                .map(this::toShortResponse));
    }

    @Override
    public RoleGroupResponse getById(Long id) {
        log.info("Viewing details of role with id '{}'", id);
        return repo.findById(id).map(this::toResponse).orElseThrow(() -> new BusinessException("error.roleGroup.notFound"));
    }

    public RoleGroupResponse create(RoleGroupRequest req) {
        log.info("Receive request to create new role");
        RoleGroup rg = new RoleGroup();
        rg.setRoleGroupName(req.getRoleGroupName());
        rg.setDescription(req.getDescription());
        rg.setRoleGroupCode(req.getRoleGroupCode());

        Set<Long> inputFunctionIds = new HashSet<>(req.getFunctionIds());
        List<Function> existingFunctions = fRepo.findAllById(inputFunctionIds);

        Set<Long> foundFunctionIds = existingFunctions.stream()
                .map(Function::getId)
                .collect(Collectors.toSet());

        Set<Long> missingFunctionIds = new HashSet<>(inputFunctionIds);
        missingFunctionIds.removeAll(foundFunctionIds);

        if (!missingFunctionIds.isEmpty()) {
            log.warn("Invalid function IDs: '{}'", missingFunctionIds);
            throw new IllegalArgumentException("Invalid function IDs: " + missingFunctionIds);
        }

        rg.setFunctions(new HashSet<>(existingFunctions));
        List<V_User> users = new ArrayList<>();
        if (req.getUserIds() != null) {
            Set<Long> inputUserIds = new HashSet<>(req.getUserIds());
            users = uRepo.findAllById(inputUserIds);
            Set<Long> foundUserIds = users.stream()
                    .map(V_User::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingUserIds = new HashSet<>(inputUserIds);
            missingUserIds.removeAll(foundUserIds);
            if (!missingUserIds.isEmpty()) {
                log.warn("Invalid user IDs: '{}'", missingUserIds);
                throw new BusinessException("error.user.notFound");
            }
        }
        RoleGroup saved = repo.save(rg);
        if (!users.isEmpty()) {
            users.forEach(u -> u.setRoleGroup(saved));
            uRepo.saveAll(users);
        }
        log.info("Created new role with id'{}'", saved.getId());
        return toResponse(saved);
    }

    public RoleGroupResponse update(Long id, RoleGroupUpdateRequest req) {
        log.info("Received request to update role with id '{}'", id);
        if (!repo.existsById(id)) {
            log.warn("Role with id '{}' does not exist", id);
            throw new BusinessException("error.roleGroup.notFound");
        }
        RoleGroup rg = repo.findById(id).get();
        String roleGroupCode = req.getRoleGroupCode();
        if(roleGroupCode!=null) rg.setRoleGroupCode(roleGroupCode);
        String roleGroupName = req.getRoleGroupName();
        if(roleGroupName!=null) rg.setRoleGroupName(roleGroupName);
        String description = req.getDescription();
        if(description!=null)rg.setDescription(description);

        if(req.getFunctionIds()!= null) {
            //some functions can be invalid
            Set<Long> inputFunctionIds = new HashSet<>(req.getFunctionIds());
            List<Function> existingFunctions = fRepo.findAllById(inputFunctionIds);

            Set<Long> foundFunctionIds = existingFunctions.stream()
                    .map(Function::getId)
                    .collect(Collectors.toSet());

            Set<Long> missingFunctionIds = new HashSet<>(inputFunctionIds);
            missingFunctionIds.removeAll(foundFunctionIds);
            if (!missingFunctionIds.isEmpty()) {
                log.warn("Invalid function IDs: '{}'", missingFunctionIds);
                throw new BusinessException("error.function.notFound");
            }
            rg.setFunctions(new HashSet<>(existingFunctions));
        }

        //some userIds might not be valid
        List<V_User> selectedUsers = new ArrayList<>();
        List<V_User> toUnassign = new ArrayList<>();
        if (req.getUserIds() != null) {
            Set<Long> selectedIds = new HashSet<>(req.getUserIds());

            List<V_User> currentUsers = uRepo.findByRoleGroupId(rg.getId());

            selectedUsers = uRepo.findAllById(selectedIds);

            Set<Long> foundIds = selectedUsers.stream().map(V_User::getId).collect(Collectors.toSet());
            Set<Long> missingIds = new HashSet<>(selectedIds);
            missingIds.removeAll(foundIds);
            if (!missingIds.isEmpty()) {
                log.warn("Invalid user IDs: '{}'", missingIds);
                throw new BusinessException("error.user.notFound");
            }
            toUnassign = currentUsers.stream()
                    .filter(u -> !selectedIds.contains(u.getId()))
                    .toList();
        }
        RoleGroup updated = repo.save(rg);

        if (!toUnassign.isEmpty()) {
            toUnassign.forEach(u -> u.setRoleGroup(null));
            uRepo.saveAll(toUnassign);
        }
        if (!selectedUsers.isEmpty()) {
            selectedUsers.forEach(u -> u.setRoleGroup(updated));
            uRepo.saveAll(selectedUsers);
        }
        log.info("Updated role");
        return toResponse(updated);
    }

    public Boolean delete(Long id) {
        log.info("Received request to delete role with id '{}'", id);
        RoleGroup roleGroup = repo.findById(id)
                .orElseThrow(() -> new BusinessException("error.roleGroup.notFound"));

        for (V_User user : roleGroup.getUsers()) {
            user.setRoleGroup(null);
            uRepo.save(user);
        }
//        repo.delete(roleGroup);
        roleGroup.setDeleted(true);
        repo.save(roleGroup);
        log.info("Deleted role with id '{}'", id);
        return true;
    }

    public RoleGroupResponse addUser(UserToAddRole req) {
        log.info("Received request to add '{}' users to role with code '{}'", req.getId().size(), req.getRoleCode());
        if (repo.findByRoleGroupCode(req.getRoleCode()) == null) {
            log.warn("Invalid role code");
            throw new BusinessException("error.roleGroup.notFound");
        }
        List<V_User> usersToAdd = uRepo.findAllById(req.getId());
        if (usersToAdd.size() != req.getId().size()) {
            log.warn("Invalid UserIds");
            throw new BusinessException("error.user.notFound");
        }
        RoleGroup rg = repo.findByRoleGroupCode(req.getRoleCode());
        usersToAdd.forEach(u -> u.setRoleGroup(rg));
        uRepo.saveAll(usersToAdd);
        return toResponse(rg);
    }

    public PageResponse<RoleGroupResponse> FILTER_ROLE(@RequestParam(required = false) String roleGroupCode,
                                                       @RequestParam(required = false) String roleGroupName,
                                                       @RequestParam(required = false) String description,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoleGroup> result = repo.filterRoleGroup(roleGroupCode, roleGroupName, description, pageable);
        log.info("Viewing filter roles, total roles: '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(this::toResponse));
    }
    public PageResponse<RoleGroupResponse> FILTER_ROLE_DTO(@RequestBody RoleFilterDto filter,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoleGroup> result = repo.filterRoleGroup(filter.getRoleGroupCode(), filter.getRoleGroupName(), filter.getDescription(), pageable);
        log.info("Viewing filter roles, total roles: '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(this::toResponse));
    }

}
