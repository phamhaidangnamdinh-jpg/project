package com.Chicken.project.dto.request.Function;

import lombok.Data;

@Data
public class FunctionUpdateRequest {
    private String functionCode;
    private String functionName;
    private String description;
}
