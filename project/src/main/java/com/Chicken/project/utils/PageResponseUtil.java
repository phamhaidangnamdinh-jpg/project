package com.Chicken.project.utils;

import com.Chicken.project.dto.response.PageResponse;
import org.springframework.data.domain.Page;

public class PageResponseUtil {
    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
