package com.Chicken.project.dto.request.Book;

import com.Chicken.project.utils.EmptyStringToNullDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;


import java.util.List;


@Data
public class BookFilterDto {
    @JsonDeserialize(using = EmptyStringToNullDeserializer.class)
    private String code;
    @JsonDeserialize(using = EmptyStringToNullDeserializer.class)
    private String title;
    @JsonDeserialize(using = EmptyStringToNullDeserializer.class)
    private String author;
    @JsonDeserialize(using = EmptyStringToNullDeserializer.class)
    private String publisher;
    private List<Long> categoryIds;
}
