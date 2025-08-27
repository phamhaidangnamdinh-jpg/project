package com.Chicken.project.service.impl;

import com.Chicken.project.dto.request.Function.FunctionFilterDto;
import com.Chicken.project.dto.request.Function.FunctionRequest;
import com.Chicken.project.dto.request.Function.FunctionUpdateRequest;
import com.Chicken.project.dto.request.User.UserNameRequest;
import com.Chicken.project.dto.response.Article.ArticleResponse;
import com.Chicken.project.dto.response.Function.FunctionResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.Article;
import com.Chicken.project.entity.Function;
import com.Chicken.project.entity.RoleGroup;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.exception.BusinessException;
import com.Chicken.project.repository.FunctionRepo;
import com.Chicken.project.repository.RoleGroupRepo;
import com.Chicken.project.repository.UserRepo;
import com.Chicken.project.service.FunctionService;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FunctionServiceImpl implements FunctionService {
    @Autowired
    private FunctionRepo repo;
    @Autowired
    private UserRepo uRepo;
    @Autowired
    private RoleGroupRepo rRepo;
    private static final Logger log = LoggerFactory.getLogger(FunctionServiceImpl.class);

    private FunctionResponse toResponse(Function func) {
        log.info("Converting permission info to response");
        FunctionResponse fr = new FunctionResponse();
        fr.setFunctionName(func.getFunctionName());
        fr.setDescription(func.getDescription());
        fr.setFunctionCode(func.getFunctionCode());
        return fr;
    }

    @Override
    public PageResponse<FunctionResponse> getAll(int page, int size) {
        log.info("Showing all permissions '{}'", page);
        Pageable pageable = PageRequest.of(page, size);
        return PageResponseUtil.fromPage(repo.findAll(pageable).map(this::toResponse));
    }

    @Override
    public FunctionResponse getById(Long id) {
        log.info("Showing details for function id '{}'", id);
        return repo.findById(id).map(this::toResponse).orElseThrow(() -> new BusinessException("error.permission.notFound"));
    }

    @Override
    public FunctionResponse create(FunctionRequest req) {
        log.info("received request to create new function code '{}'", req.getFunctionCode());
        if (repo.existsByFunctionCode(req.getFunctionCode())) {
            log.warn("Permission with code '{}' already exists", req.getFunctionCode());
            throw new BusinessException("error.permission.existed");
        }
        Function func = new Function();
        func.setFunctionCode(req.getFunctionCode());
        func.setDescription(req.getDescription());
        func.setFunctionName(req.getFunctionName());
        log.info("Created new permission");
        return toResponse(repo.save(func));
    }

    @Override
    public FunctionResponse update(Long id, FunctionUpdateRequest req) {
        log.info("Received request to update function with id '{}'", id);
        if (!repo.existsById(id)) {
            log.warn("Permission with id '{}' does not exist", id);
            throw new BusinessException("error.permission.notFound");
        }
        Function func = repo.findById(id).orElseThrow(() -> new BusinessException("error.permission.notFound"));
        String functionName = req.getFunctionName();
        if(functionName!=null) func.setFunctionName(functionName);
        String functionCode = req.getFunctionCode();
        if(functionCode!=null) func.setFunctionCode(functionCode);
        String description = req.getDescription();
        if(description!=null)  func.setDescription(description);
        log.info("updated permission");
        return toResponse(repo.save(func));
    }

    @Override
    public Boolean delete(Long id) {
        log.info("Received request to delete function with id '{}'", id);
        if (!repo.existsById(id)) {
            log.warn("Permission with id '{}' does not exist", id);
            throw new BusinessException("error.permission.notFound");
        }
        Function function = repo.findById(id).get();
        if (function.getRoleGroups() != null) {
            List<RoleGroup> rg = function.getRoleGroups().stream().toList();
            rg.forEach(r -> {
                r.getFunctions().remove(function);
                rRepo.save(r);
            });
        }
//        repo.delete(function);
        function.setDeleted(true);
        repo.save(function);
        log.info("Deleted permission");
        return true;
    }

    public List<FunctionResponse> findByUser(UserNameRequest req) {
        log.info("Received request to find permissions of user '{}'", req.getUsername());
        if (!uRepo.existsByUsername(req.getUsername())) {
            log.warn("Username '{}' does not exist", req.getUsername());
            throw new BusinessException("error.user.notFound");
        }
        V_User user = uRepo.findByUsername(req.getUsername());
        List<Function> functions = user.getRoleGroup().getFunctions().stream().toList();
        log.info("Showing permissions of user '{}'", req.getUsername());
        return functions.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PageResponse<FunctionResponse> FILTER_FUNCTION(@RequestParam(required = false) String functionCode,
                                                          @RequestParam(required = false) String functionName,
                                                          @RequestParam(required = false) String description,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {
        log.info("Received request to view filtered permissions");
        Pageable pageable = PageRequest.of(page, size);
        Page<Function> result = repo.filterFunction(functionCode, functionName, description, pageable);
        log.info("Viewing filtered permissions, total permissions: '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(this::toResponse));
    }
    public PageResponse<FunctionResponse> FILTER_FUNCTION_DTO(@RequestBody FunctionFilterDto filter,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "5") int size) {
        log.info("Received request to view filtered permissions");
        Pageable pageable = PageRequest.of(page, size);
        Page<Function> result = repo.filterFunction(filter.getFunctionCode(), filter.getFunctionName(), filter.getDescription(), pageable);
        log.info("Viewing filtered permissions, total permissions: '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(this::toResponse));
    }
}
