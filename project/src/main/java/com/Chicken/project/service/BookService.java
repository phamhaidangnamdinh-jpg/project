package com.Chicken.project.service;

import com.Chicken.project.dto.request.Book.BookFilterDto;
import com.Chicken.project.dto.request.Book.BookRequest;
import com.Chicken.project.dto.request.Book.BookUpdateRequest;
import com.Chicken.project.dto.response.Book.BookResponse;
import com.Chicken.project.dto.response.Book.ShortBookResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.Book;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public interface BookService {
    public ShortBookResponse toShortResponse(Book book);

//    public PageResponse<ShortBookResponse> getFiltered(@RequestParam(required = false) String code,
//                                                       @RequestParam(required = false) String title,
//                                                       @RequestParam(required = false) String author,
//                                                       @RequestParam(required = false) String publisher,
//                                                       @RequestParam(required = false) String printType,
//                                                       @RequestParam(required = false) String language,
//                                                       @RequestParam(required = false) String description,
//                                                       @RequestParam(required = false) Integer minPage,
//                                                       @RequestParam(required = false) Integer maxPage,
//                                                       @RequestParam(required = false) Integer minQuantity,
//                                                       @RequestParam(required = false) Integer maxQuantity,
//                                                       @RequestParam(defaultValue = "0") int page,
//                                                       @RequestParam(defaultValue = "5") int size
//    );
    public BookResponse toResponse(Book book);
    public BookResponse createBook(BookRequest request);

    PageResponse<ShortBookResponse> viewBook(int page, int size);
    boolean deleteBook(long id);
    BookResponse getBookById(long id);

    Page<BookResponse> searchBook(String keyword, Pageable pageable);
    PageResponse<BookResponse> filterBook(@RequestBody BookFilterDto filter,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size);

    BookResponse updateBook(long id, BookUpdateRequest request);
    ByteArrayInputStream exportBook(BookFilterDto filter) throws IOException;

    String importBook(MultipartFile file);

    void exportFilteredBooks(String code, String title, String author, String publisher, HttpServletResponse response) throws IOException;
}
