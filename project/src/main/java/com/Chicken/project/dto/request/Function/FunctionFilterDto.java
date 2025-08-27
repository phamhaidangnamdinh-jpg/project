package com.Chicken.project.dto.request.Function;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class FunctionFilterDto {
    private String functionCode;
    private String functionName;
    private String description;
}
