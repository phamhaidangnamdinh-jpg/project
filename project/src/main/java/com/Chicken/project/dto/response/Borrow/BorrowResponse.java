package com.Chicken.project.dto.response.Borrow;

import com.Chicken.project.entity.Borrow.BorrowStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BorrowResponse {
    private String userName;
    private String bookTitle;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    LocalDate borrowDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    LocalDate returnDate;
    private BorrowStatus status;
}
