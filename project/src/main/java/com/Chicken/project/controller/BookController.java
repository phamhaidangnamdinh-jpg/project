package com.Chicken.project.controller;


import com.Chicken.project.config.ResponseConfig;
import com.Chicken.project.dto.request.Book.BookFilterDto;
import com.Chicken.project.dto.request.Book.BookRequest;
import com.Chicken.project.dto.request.Book.BookUpdateRequest;
import com.Chicken.project.dto.response.ApiResponse;
import com.Chicken.project.dto.response.Book.BookResponse;
import com.Chicken.project.dto.response.Book.ShortBookResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.service.impl.BookServiceImpl;
import com.Chicken.project.service.impl.BookSyncServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/library/book")
public class BookController {
    @Autowired
    private BookServiceImpl service;
    @Autowired
    private BookSyncServiceImpl syncService;
    private final ResponseConfig responseConfig;
//    @GetMapping("/test")
//    public ResponseEntity<String> test(Authentication auth) {
//        System.out.println("👤 Principal = " + auth.getPrincipal());
//        System.out.println("🔐 Authorities = " + auth.getAuthorities());
//        return ResponseEntity.ok("You are authenticated");
//    }

    @GetMapping("/sync")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<Object>> syncBooks(HttpServletRequest request) {
        int added = syncService.syncGoogleBooks();
        return responseConfig.success("ROLE_SYNC_BOOK", "book.sync.success", new Object[]{added},null);
    }

    @PostMapping("/create")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<?> createBook(@Valid @RequestBody BookRequest request) {
        BookResponse res = service.createBook(request);
        return responseConfig.success("ROLE_CREATE_BOOK","book.create.success",  new Object[]{request.getTitle()}, res);
    }
    @GetMapping("")
//    @PreAuthorize("hasRole('VIEW_BOOK')")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<?>>> viewBook(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return responseConfig.success("ROLE_VIEW_BOOK", "book.view.success", null, service.viewBook(page, size));
    }
    @GetMapping("/{id}/detail")
//    @PreAuthorize("hasRole('VIEW_BOOK')")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<BookResponse>> getDetail(@PathVariable Long id, HttpServletRequest request) {
        return responseConfig.success("ROLE_VIEW_BOOK","book.detail.success", new Object[]{id}, service.getBookById(id));
    }
    @PutMapping("{ip}/update")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<BookResponse>> update(@PathVariable Long id, HttpServletRequest request, @Valid @RequestBody BookUpdateRequest req) {
        return responseConfig.success("ROLE_UPDATE_BOOK","book.update.success", new Object[]{id}, service.updateBook(id, req));
    }
//    @GetMapping("/search")
//    @PreAuthorize("fileRole(#request)")
//    public ResponseEntity<ApiResponse<Page<BookResponse>>> searchBook(@RequestParam String keyword, @RequestParam int page, HttpServletRequest request) {
//        return ResponseConfig.success("ROLE_VIEW_BOOK","page "+page, service.searchBook(keyword, PageRequest.of(page,2)));
//    }
    @GetMapping("/search")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> filterBooks(HttpServletRequest request, @RequestBody BookFilterDto filter, @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "5") int size) {
        return responseConfig.success("ROLE_VIEW_BOOK","book.filter.success", null, service.filterBook(filter, page, size));
    }
    @DeleteMapping("{id}/delete")
//    @PreAuthorize("hasRole('DELETE_BOOK')")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<Objects>> deleteBook(@PathVariable Long id, HttpServletRequest request) {
        if (service.deleteBook(id)) {
            String message = "Book " + id + " has been deleted";
            return responseConfig.success("ROLE_DELETE_BOOK", "book.delete.success", null, null);
        } else return responseConfig.error("ROLE_DELETE_BOOK", "book.delete.failure");
    }
    @GetMapping("export")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<InputStreamResource> exportBook(@RequestBody BookFilterDto filter, HttpServletRequest request) throws IOException {
        return responseConfig.downloadFile("books.csv", service.exportBook(filter));
    }

    @GetMapping("/filter/export")
    @PreAuthorize("fileRole(#request)")
    public void exportFilteredBooks(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            HttpServletResponse response, HttpServletRequest request
    ) throws IOException {
        service.exportFilteredBooks(code, title, author, publisher, response);
    }
    @PostMapping("/import")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<List<String>>> importBooks(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        String number = service.importBook(file);
        return responseConfig.success("ROLE_IMPORT_BOOK", "book.import.success" +":"+number, new Object[]{number}, null);
    }

    @PostMapping("/import-excel")
    @PreAuthorize("fileRole(#request)")
    public ResponseEntity<ApiResponse<List<String>>> importBooksExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        String number = service.importBookFromExcel(file);
        return responseConfig.success("ROLE_IMPORT_BOOK", "book.import.success" +":"+number, new Object[]{number}, null);
    }

    @GetMapping("/export-excel")
    @PreAuthorize("fileRole(#request)")
    public void exportFilteredBooks(
            @RequestBody(required = false) BookFilterDto filter,
            HttpServletResponse response, HttpServletRequest request
    ) throws IOException {
        service.exportBookToExcel(filter, response);
    }
//    @GetMapping("/filter")
//    @PreAuthorize("fileRole(#request)")
//    public ResponseEntity<ApiResponse<PageResponse<ShortBookResponse>>> importBooks(HttpServletRequest request, @RequestParam(required = false) String code,
//                                                                            @RequestParam(required = false) String title,
//                                                                            @RequestParam(required = false) String author,
//                                                                            @RequestParam(required = false) String publisher,
//                                                                            @RequestParam(required = false) String printType,
//                                                                            @RequestParam(required = false) String language,
//                                                                            @RequestParam(required = false) String description,
//                                                                            @RequestParam(required = false) Integer minPage,
//                                                                            @RequestParam(required = false) Integer maxPage,
//                                                                            @RequestParam(required = false) Integer minQuantity,
//                                                                            @RequestParam(required = false) Integer maxQuantity,
//                                                                            @RequestParam(defaultValue = "0") int page,
//                                                                            @RequestParam(defaultValue = "5") int size){
//        return ResponseConfig.success("ROLE_VIEW_BOOK", "filtered list", service.getFiltered(code, title, author, publisher, printType, language, description, minPage, maxPage, minQuantity, maxQuantity, page, size));
//    }
}
