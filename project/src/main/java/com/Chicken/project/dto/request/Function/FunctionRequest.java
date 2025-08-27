package com.Chicken.project.dto.request.Function;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class FunctionRequest {
    @NotBlank(message = "{function.code.notEmpty}")
    private String functionCode;

    @NotBlank(message = "{function.name.notEmpty}")
    private String functionName;

    @NotBlank(message = "{function.description.notEmpty}")
    private String description;
}
