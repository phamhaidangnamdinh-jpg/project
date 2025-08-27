package com.Chicken.project.dto.request.Borrow;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Data
public class BorrowFilterRequest {
    private String user;
    private String bookName;
    private String bookCode;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate borrowDateMin;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate borrowDateMax;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate returnDateMin;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate returnDateMax;
}
