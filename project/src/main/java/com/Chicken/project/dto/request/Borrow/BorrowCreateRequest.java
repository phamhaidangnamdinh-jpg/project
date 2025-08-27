package com.Chicken.project.dto.request.Borrow;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BorrowCreateRequest {
    @Min(value = 1, message = "{borrow.userId.notEmpty}")
    private long user_id;

    @Min(value = 1, message = "{borrow.bookId.notEmpty}")
    private long book_id;

    @NotNull(message = "{borrow.returnDate.notEmpty}")
    @FutureOrPresent(message = "{borrow.returnDate.futureOrPresent}")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate returnDate;
}
