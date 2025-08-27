package com.Chicken.project.service;

import com.Chicken.project.dto.request.Function.FunctionRequest;
import com.Chicken.project.dto.request.Function.FunctionUpdateRequest;
import com.Chicken.project.dto.request.User.UserNameRequest;
import com.Chicken.project.dto.response.Function.FunctionResponse;
import com.Chicken.project.dto.response.PageResponse;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface FunctionService {
    PageResponse<FunctionResponse> getAll(int page, int size);
    FunctionResponse getById(Long id);
    FunctionResponse create(FunctionRequest req);
    FunctionResponse update(Long id, FunctionUpdateRequest req);
    Boolean delete(Long id);
    List<FunctionResponse> findByUser(UserNameRequest req);
    PageResponse<FunctionResponse> FILTER_FUNCTION(@RequestParam(required = false) String functionCode,
                                                   @RequestParam(required = false) String functionName,
                                                   @RequestParam(required = false) String description,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "5") int size);
}
