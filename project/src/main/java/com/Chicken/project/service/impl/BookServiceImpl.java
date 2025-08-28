package com.Chicken.project.service.impl;

import com.Chicken.project.dto.request.Book.BookFilterDto;
import com.Chicken.project.dto.request.Book.BookRequest;
import com.Chicken.project.dto.request.Book.BookUpdateRequest;
import com.Chicken.project.dto.response.Book.BookResponse;
import com.Chicken.project.dto.response.Book.ShortBookResponse;
import com.Chicken.project.dto.response.Category.ShortCategoryResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.Article;
import com.Chicken.project.entity.Book;
import com.Chicken.project.entity.Category;
import com.Chicken.project.entity.Comment;
import com.Chicken.project.exception.BusinessException;
import com.Chicken.project.repository.ArticleRepo;
import com.Chicken.project.repository.BookRepository;
import com.Chicken.project.repository.CategoryRepository;
import com.Chicken.project.repository.CommentRepo;
import com.Chicken.project.service.BookService;
import com.Chicken.project.utils.PageResponseUtil;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookRepository repo;
    @Autowired
    CategoryRepository crepo;
    @Autowired
    CommentRepo coRepo;
    @Autowired
    ArticleRepo aRepo;
    private static final Logger log =  LoggerFactory.getLogger(BookServiceImpl.class);
    public ShortBookResponse toShortResponse(Book book){
        log.info("Converting book id '{}' into short response", book.getId());
        ShortBookResponse res = new ShortBookResponse();
        res.setAuthor(book.getAuthor());
        res.setId(book.getId());
        res.setCode(book.getCode());
        res.setTitle(book.getTitle());
        return res;
    }


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
//                                               ){
//        Specification<Book> spec = ((root, query, criteriaBuilder) -> null);
//            if(code!=null){
//                spec = spec.and(BookSpecification.hasCode(code));
//            }
//        if(author!=null){
//            spec = spec.and(BookSpecification.hasAuthor(author));
//        }
//        if(title!=null){
//            spec = spec.and(BookSpecification.hasTitle(title));
//        }
//        if(publisher!=null){
//            spec = spec.and(BookSpecification.hasPublisher(publisher));
//        }
//        if(printType!=null){
//            spec = spec.and(BookSpecification.hasPrintType(printType));
//        }
//        if(language!=null){
//            spec = spec.and(BookSpecification.hasLanguage(language));
//        }
//        if(description!=null){
//            spec = spec.and(BookSpecification.hasDescription(description));
//        }
//        if(minPage!=null){
//            spec = spec.and(BookSpecification.minPage(minPage));
//        }
//        if(maxPage!=null){
//            spec = spec.and(BookSpecification.minPage(maxPage));
//        }
//        if(minQuantity!=null){
//            spec = spec.and(BookSpecification.minPage(minQuantity));
//        }
//        if(maxQuantity!=null){
//            spec = spec.and(BookSpecification.minPage(maxQuantity));
//        }
//        Pageable pageable = PageRequest.of(page, size);
//        Page<ShortBookResponse> result = repo.findAll(spec, pageable).map(this::toShortResponse);
//        return PageResponseUtil.fromPage(result);
//    }


    public BookResponse toResponse(Book book) {
        log.info("Converting book id '{}' into response", book.getId());
        BookResponse res = new BookResponse();
        res.setId(book.getId());
        res.setAuthor(book.getAuthor());
        res.setTitle(book.getTitle());
        res.setPublisher(book.getPublisher());
        res.setCode(book.getCode());
        res.setDescription(book.getDescription());
        res.setLanguage(book.getLanguage());
        res.setPageCount(book.getPageCount());
        res.setPrintType(book.getPrintType());
        res.setIsBorrowed(book.getIsBorrowed());
        List<ShortCategoryResponse> catRes = book.getCategories().stream().map(cat -> {
            ShortCategoryResponse cr = new ShortCategoryResponse();
            cr.setId(cat.getId());
            cr.setCode(cat.getCode());
            cr.setName(cat.getName());
            return cr;
        }).toList();

        res.setCategories(catRes);
        return res;
    }
    @Override
    public BookResponse createBook(BookRequest request) {
        log.info("Received request to create new book with code '{}'", request.getCode());
        if(repo.existsByCode(request.getCode())) {
            log.warn("Book code '{}' already exists", request.getCode());
            throw new BusinessException("error.book.existed");
        }
            Book book = new Book();
            book.setAuthor(request.getAuthor());
            book.setTitle(request.getTitle());
            book.setPublisher(request.getPublisher());
            book.setCode(request.getCode());
            book.setDescription(request.getDescription());
            book.setLanguage(request.getLanguage());
            book.setPageCount(request.getPageCount());
            book.setPrintType(request.getPrintType());
        List<Long> inputCategoryIds = request.getCategoryIds();
        List<Category> categories = crepo.findAllById(inputCategoryIds);

        Set<Long> foundIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        List<Long> missingIds = inputCategoryIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());

        if (!missingIds.isEmpty()) {
            log.warn("Some invalid Category ids: {}", missingIds);
            throw new BusinessException("error.category.notFound");
        }
        if (categories.size() != inputCategoryIds.size()) {
            log.warn("Invalid Category ids");
            throw new BusinessException("error.category.not.found");
        }
        book.setCategories(categories);
        book = repo.save(book);
        log.info("Book created with ID: {}", book.getId());
            return toResponse(book);
    }

    public PageResponse<ShortBookResponse> viewBook(int page, int size) {
        log.info("Showing list of book page '{}'", page);
        Pageable pageable = PageRequest.of(page, size);
        return PageResponseUtil.fromPage(repo.findAll(pageable)
                .map(this::toShortResponse));
    }
    public boolean deleteBook(long id) {
        log.info("Received request to delete Book id '{}'", id);
        if(repo.existsById(id)) {
            Book book = repo.findById(id).get();
            List<Article> articles = book.getArticles();
            for (Article article : book.getArticles()) {
                for (Comment comment : article.getComments()) {
                    log.info("Deleted comment id '{}'", article.getId());
                    comment.setDeleted(true);
                    coRepo.save(comment);
                }
                article.setDeleted(true);
                aRepo.save(article);
                log.info("Deleted article id '{}'", article.getId());
            }
//            repo.deleteById(id);
            book.setDeleted(true);
            repo.save(book);
            log.info("Deleted book id '{}'", book.getId());
            return true;
        }
        else {
            log.warn("Book doesn't exists");
            return false;
        }
    }
    public BookResponse getBookById(long id) {
        if(!repo.existsById(id)) {
            log.warn("Book doesn't exists");
            throw new BusinessException("error.book.notFound");
        }
        log.info("Showing book id '{}'", id);
        return toResponse(repo.findById(id).orElseThrow());
    }

    public Page<BookResponse> searchBook(String keyword, Pageable pageable) {
        Page<Book> books = repo.filterBook(keyword, pageable);
        log.info("Showing books with keyword '{}'", keyword);
        return books.map(this::toResponse);
    }
    public PageResponse<BookResponse> filterBook(@RequestBody BookFilterDto filter,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> result = repo.filterBook(filter.getCode(), filter.getTitle(), filter.getAuthor(), filter.getPublisher(),filter.getCategoryIds(), pageable);
        Page<BookResponse> mappedPage = result.map(this::toResponse);
        log.info("Showing filtered books, total book: {}", result.getTotalElements());
        return PageResponseUtil.fromPage(mappedPage);
    }

    public BookResponse updateBook(long id, BookUpdateRequest request) {
        log.info("Received request to update book id '{}'", id);
        if(!repo.existsById(id)) {
            log.warn("Book doesn't exists");
            throw new BusinessException("error.book.notFound");
        }
        Book book = repo.findById(id).get();

        String author = request.getAuthor();
        if(author != null)book.setAuthor(author);
        String title = request.getTitle();
        if(title != null) book.setTitle(title);
        String publisher = request.getPublisher();
        if( publisher != null) book.setPublisher(publisher);
        String code = request.getCode();
        if(code != null) book.setCode(code);
        String description = request.getDescription();
        if(description != null) book.setDescription(description);
        String language = request.getLanguage();
        if(language != null) book.setLanguage(language);
        Integer pageCount = request.getPageCount();
        if(pageCount != null) book.setPageCount(pageCount);
        String printType = request.getPrintType();
        if(printType != null)book.setPrintType(printType);
        Integer quantity = request.getQuantity();
        if(quantity != null)book.setQuantity(quantity);
        List<Long> catIds = request.getCategoryIds();

        if(catIds != null) {
            if (!catIds.isEmpty()) {
                List<Category> categories = crepo.findAllById(catIds);

                if (categories.size() != catIds.size()) {
                    log.warn("Invalid Categories");
                    throw new BusinessException("error.category.notFound");
                }

                book.setCategories(categories);
            } else {
                book.setCategories(Collections.emptyList());
            }
        }
        log.info("Updated book '{}'", book.getTitle());
        return toResponse(repo.save(book));
    }
    public ByteArrayInputStream exportBook(BookFilterDto filter) throws IOException {
        log.info("Received request to export book to csv");
        Pageable pageable = Pageable.unpaged();
        Page<Book> books = repo.filterBook(filter.getCode(), filter.getTitle(), filter.getAuthor(), filter.getPublisher(), filter.getCategoryIds(), pageable);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))){
            writer.writeNext(new String[]{"code", "title", "author", "publisher", "description", "language", "pageCount", "printType"});
            for(Book book : books.getContent()) {
                writer.writeNext(new String[]{
                        book.getCode(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher(),
                        book.getLanguage(),
                        Integer.toString(book.getPageCount()),
                        book.getPrintType()});
            }

        }
        log.info("Exporting books to csv");
        return new ByteArrayInputStream(out.toByteArray());
    }

    public String importBook(MultipartFile file) {
        log.info("Received request to import book from csv");
        if(file.isEmpty()) {
            log.warn("Empty csv file");
            throw new BusinessException("error.file.empty");
        }
        List<Book> books = new ArrayList<>();
        List<String[]> errorRows = new ArrayList<>();
        int lineNumber = 1;

        try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            CSVReaderHeaderAware csvreader = new CSVReaderHeaderAware(reader)) {

            Map<String, String> row;

            while((row= csvreader.readMap())!=null){
                Map<String, String> row1 = new HashMap();

                for(Map.Entry<String,String> entry: row.entrySet()){
                    row1.put(entry.getKey().toLowerCase(), entry.getValue());
                }
                try {
                    String code = row1.getOrDefault("code", "");
                    if (code.isEmpty()) {
                        throw new IllegalArgumentException("Missing 'code'");
                    }
                    Book book;
                    if(!repo.existsByCode(code)) book = new Book();
                    else book = repo.findByCode(code);
                    book.setTitle(row1.getOrDefault("title", ""));
                    book.setAuthor(row1.getOrDefault("author", ""));
                    book.setPublisher(row1.getOrDefault("publisher", ""));
                    book.setCode(code);
                    book.setIsBorrowed(0);
                    book.setQuantity(Integer.parseInt(row1.getOrDefault("quantity", "0")));
                    book.setDescription(row1.getOrDefault("description", ""));
                    book.setLanguage(row1.getOrDefault("language", ""));
                    book.setPageCount(Integer.parseInt(row1.getOrDefault("pagecount", "0")));
                    book.setPrintType(row1.getOrDefault("printType", ""));
                    books.add(book);
                } catch (Exception ex) {
                    errorRows.add(new String[]{
                            "Line " + lineNumber,
                            ex.getMessage()
                    });
                }
                lineNumber++;
            }
            repo.saveAll(books);
            if (!errorRows.isEmpty()) {
                return errorRows.toString();
            }
            log.info("Imported books from csv");
            return "imported"+ books.size() +"books";

        } catch (IOException e) {
            log.warn("Invalid file format");
            throw new BusinessException("error.file.error");
        } catch (CsvValidationException e) {
            log.error("Unable to read file");
            throw new BusinessException("error.file.read");
        }
    }



    public void exportFilteredBooks(String code, String title, String author, String publisher, HttpServletResponse response) throws IOException {
        log.info("Received request to export filtered book to csv");
        List<Book> books = repo.filterBook(code, title, author, publisher);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=filtered_books.csv");
        log.info("Exporting filtered books to csv");
        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(new String[]{"Code", "Title", "Publisher", "Author", "Description", "Language", "PageCount", "PrintType"});
            for (Book b : books) {
                writer.writeNext(new String[]{
                        b.getCode(),
                        b.getTitle(),
                        b.getPublisher(),
                        b.getAuthor(),
                        b.getDescription(),
                        b.getLanguage(),
                        String.valueOf(b.getPageCount()),
                        b.getPrintType()
                });
            }
        }
    }

    public void exportFilteredBooksWithDto(BookFilterDto filter, HttpServletResponse response) throws IOException {
        log.info("Received request to export filtered book to csv");
        List<Book> books = repo.filterBook(filter.getCode(), filter.getTitle(), filter.getAuthor(), filter.getPublisher(), filter.getCategoryIds());

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=filtered_books.csv");
        log.info("Exporting filtered books to csv");
        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(new String[]{"Code", "Title", "Publisher", "Author", "Description", "Language", "PageCount", "PrintType"});
            for (Book b : books) {
                writer.writeNext(new String[]{
                        b.getCode(),
                        b.getTitle(),
                        b.getPublisher(),
                        b.getAuthor(),
                        b.getDescription(),
                        b.getLanguage(),
                        String.valueOf(b.getPageCount()),
                        b.getPrintType()
                });
            }
        }
    }

    public String importBookFromExcel(MultipartFile file) throws IOException {
        log.info("Received request to import book from excel");
        if(file.isEmpty()) {
            log.warn("Empty file");
            throw new BusinessException("error.file.empty");
        }
        List<Book> books = new ArrayList<>();
        List<String[]> errorRows = new ArrayList<>();
        int lineNumber = 1;

        try(InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is)){
            Sheet sheet = workbook.getSheetAt(0);
            if(sheet == null){
                throw new BusinessException("error.file.error");
            }
            Row headerRow = sheet.getRow(0);
            if(headerRow == null){
                throw new BusinessException("error.file.error");
            }
            Map<Integer, String> headers = new HashMap<>();
            for (Cell cell : headerRow) {
                headers.put(cell.getColumnIndex(), cell.getStringCellValue().toLowerCase());
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                lineNumber = i + 1;
                try{
                    Map<String, String> rowMap = new HashMap<>();
                    for (Cell cell : row) {
                        String header = headers.get(cell.getColumnIndex());
                        if (header != null) {
                            cell.setCellType(CellType.STRING);
                            rowMap.put(header, cell.getStringCellValue().trim());
                        }
                    }
                    String code = rowMap.getOrDefault("code", "");
                    if (code.isEmpty()) {
                        throw new IllegalArgumentException("Missing 'code'");
                    }
                    Book book;
                    if(!repo.existsByCode(code)) book = new Book();
                    else book = repo.findByCode(code);
                    book.setTitle(rowMap.getOrDefault("title", ""));
                    book.setAuthor(rowMap.getOrDefault("author", ""));
                    book.setPublisher(rowMap.getOrDefault("publisher", ""));
                    book.setCode(code);
                    book.setIsBorrowed(0);
                    book.setQuantity(Integer.parseInt(rowMap.getOrDefault("quantity", "0")));
                    book.setDescription(rowMap.getOrDefault("description", ""));
                    book.setLanguage(rowMap.getOrDefault("language", ""));
                    book.setPageCount(Integer.parseInt(rowMap.getOrDefault("pagecount", "0")));
                    book.setPrintType(rowMap.getOrDefault("printtype", ""));

                    books.add(book);
                }catch (Exception ex) {
                    errorRows.add(new String[]{
                            "Line " + lineNumber,
                            ex.getMessage()
                    });

                }
        }
            repo.saveAll(books);

            if (!errorRows.isEmpty()) {
                return errorRows.toString();
            }

            log.info("Imported {} books from Excel", books.size());
            return "Imported " + books.size() + " books";
    } catch (IOException e) {
            log.warn("Invalid file format", e);
            throw new BusinessException("error.file.error");
        }
}

    public void exportBookToExcel( BookFilterDto filter, HttpServletResponse response) throws IOException {
        log.info("Received request to export filtered book to Excel");

        if (filter == null) {
            filter = new BookFilterDto();
        }
        List<Book> books = repo.filterBook(
                filter.getCode(),
                filter.getTitle(),
                filter.getAuthor(),
                filter.getPublisher(),
                filter.getCategoryIds()
        );
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=filtered_books.xlsx");
        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Books");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Code", "Title", "Author", "Description", "Borrowed", "Language", "Page count",
                    "Print type", "Publisher", "Quantity", "Created by", "Created at"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            int rowIdx = 1;
            for(Book b : books){
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(b.getCode());
                row.createCell(1).setCellValue(b.getTitle());
                row.createCell(2).setCellValue(b.getAuthor());
                row.createCell(3).setCellValue(b.getDescription());
                row.createCell(4).setCellValue(b.getIsBorrowed());
                row.createCell(5).setCellValue(b.getLanguage());
                row.createCell(6).setCellValue(b.getPageCount());
                row.createCell(7).setCellValue(b.getPrintType());
                row.createCell(8).setCellValue(b.getPublisher());
                row.createCell(9).setCellValue(b.getQuantity());
                row.createCell(10).setCellValue(b.getCreatedBy());
                row.createCell(11).setCellValue(b.getCreatedDate());
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            try (ServletOutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }
        }
        log.info("Exported {} books to Excel", books.size());
    }
}
