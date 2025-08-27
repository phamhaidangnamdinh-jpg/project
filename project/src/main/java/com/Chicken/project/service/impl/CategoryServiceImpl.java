package com.Chicken.project.service.impl;

import com.Chicken.project.dto.request.Category.AddOrRemoveCategoryRequest;
import com.Chicken.project.dto.request.Category.CategoryFilterDto;
import com.Chicken.project.dto.request.Category.CategoryRequest;
import com.Chicken.project.dto.request.Category.CategoryUpdateRequest;
import com.Chicken.project.dto.response.Book.ShortBookResponse;
import com.Chicken.project.dto.response.Category.CategoryResponse;
import com.Chicken.project.dto.response.Category.CategoryStatisticResponse;
import com.Chicken.project.dto.response.Category.ShortCategoryResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.Book;
import com.Chicken.project.entity.Category;
import com.Chicken.project.exception.BusinessException;
import com.Chicken.project.repository.BookRepository;
import com.Chicken.project.repository.CategoryRepository;
import com.Chicken.project.utils.PageResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl {
    @Autowired
    CategoryRepository repo;
    @Autowired
    BookRepository brepo;
    private static final Logger log =  LoggerFactory.getLogger(CategoryServiceImpl.class);
    public ShortCategoryResponse toShortResponse(Category cat){
        log.info("Converting category id '{}' into short response", cat.getId());
        ShortCategoryResponse res = new ShortCategoryResponse();
        res.setCode(cat.getCode());
        res.setName(cat.getName());
        res.setId(cat.getId());
        return res;
    }
    public CategoryResponse toResponse(Category category) {
        log.info("Converting category id '{}' into response", category.getId());
        CategoryResponse res = new CategoryResponse();
        res.setId(category.getId());
        res.setCode(category.getCode());
        res.setName(category.getName());
        res.setDescription(category.getDescription());

        List<ShortBookResponse> bookResponses = category.getBooks().stream().map(book -> {
            ShortBookResponse br = new ShortBookResponse();
            br.setId(book.getId());
            br.setCode(book.getCode());
            br.setTitle(book.getTitle());
            br.setAuthor(book.getAuthor());
            return br;
        }).toList();
        res.setBooks(bookResponses);
        return res;
    }
@Transactional
    public boolean deleteCategory(long id) {
        log.info("Getting category to delete");
        if(!repo.existsById(id)){
            log.warn("Category id '{}' does not exist", id);
            throw new BusinessException("error.category.notFound");
        }
        Category category = repo.findById(id).get();
        List<Long> bookIds = repo.getBookId(id);
        List<Book> books = bookIds.stream()
                .map(bid -> brepo.findById(bid).orElse(null))
                .filter(Objects::nonNull)
                .toList();
        for(Book book : books) {
            log.info("Removing link between book id '{}' and category", book.getId());
            book.getCategories().remove(category);
        }
        brepo.saveAll(books);
        category.setDeleted(true);
        repo.save(category);
        log.info("Deleted category id '{}'", id);
//        repo.deleteById(id);
        return true;
    }
    public PageResponse<ShortCategoryResponse> getCategories(int page, int size) {
        log.info("Getting all categories page '{}'", page);
        Pageable pageable = PageRequest.of(page, size);
        return PageResponseUtil.fromPage(repo.findAll(pageable).map(this::toShortResponse));
    }
    public CategoryResponse getDetail(long id){
        log.info("Getting info for category id '{}'", id);
        return toResponse(repo.findById(id).orElseThrow(() -> new BusinessException("error.category.notFound")));
    }
    public CategoryResponse createCategory(CategoryRequest res) {
        log.info("Received request to create new category");
        if(repo.existsByCode(res.getCode())) {
            log.warn("Category with code '{}' already exists", res.getCode());
            throw new BusinessException("error.category.existed");
        }
        Category cat = new Category();
        cat.setName(res.getName());
        cat.setDescription(res.getDescription());
        cat.setCode(res.getCode());
        Category catt = repo.save(cat);
        log.info("Created new category with id '{}'", catt.getId());
        return toResponse(catt);
    }
    public CategoryResponse updateCategory(CategoryUpdateRequest res, long id) {
        log.info("Received request to update category with id '{}'", id);
        if(!repo.existsById(id)) {
            log.warn("Can't find category with id '{}'", id);
            throw new BusinessException("error.category.notFound");
        }
        else{
            Category cat = repo.findById(id).get();
            String code =res.getCode();
            if(code!= null) cat.setCode(code);
            String name = res.getName();
            if(name != null) cat.setName(name);
            String description = res.getDescription();
            if(description != null) cat.setDescription(description);

            repo.save(cat);
            return toResponse(repo.save(cat));
        }
    }
    public CategoryResponse AddOrRemoveFromCategory(AddOrRemoveCategoryRequest req, long id){
        log.info("Received request to add '{}' books and remove '{}' books from category id '{}'", req.getAddIds().size(), req.getRemoveIds().size(), id);
        if(!repo.existsById(id)) {
            log.warn("Can't find category with id '{}'", id);
            throw new BusinessException("error.category.notFound");
        }
        Category cat = repo.findById(id).get();
        if (req.getAddIds() != null && !req.getAddIds().isEmpty()) {
            List<Book> booksToAdd = brepo.findAllById(req.getAddIds());
            if (booksToAdd.size() != req.getAddIds().size()) {
                throw new BusinessException("error.book.notFound");
            }
            for (Book book : booksToAdd) {
                book.getCategories().add(cat);
            }
            brepo.saveAll(booksToAdd);
        }
        if(req.getRemoveIds() != null && !req.getRemoveIds().isEmpty()){
            List<Book> booksToRemove = brepo.findAllById(req.getRemoveIds());
            if (booksToRemove.size()!= req.getRemoveIds().size()){
                throw new BusinessException("error.book.notFound");
            }
            for (Book book : booksToRemove) {
                book.getCategories().remove(cat);
            }
            brepo.saveAll(booksToRemove);
        }
    return toResponse(cat);
    }

    public List<CategoryStatisticResponse> getBookStatistic(){
        log.info("Getting book statistics");
        return brepo.getBookCountByCategory().stream().map(obj -> new CategoryStatisticResponse((String) obj[0], (Long) obj[1])).collect(Collectors.toList());
    }

    public PageResponse<ShortCategoryResponse> filter(@RequestParam(required = false) String code,
                                               @RequestParam(required = false) String description,
                                               @RequestParam(required = false) String name,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size){
        log.info("Receive request to view filtered categories");
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> result = repo.filterCategory(code, description, name, pageable);
        log.info("Showing filtered filtered categories, total categories: '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(this::toShortResponse));

    }
    public PageResponse<ShortCategoryResponse> search(@RequestBody CategoryFilterDto filter,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "5") int size){
        log.info("Receive request to view filtered categories");
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> result = repo.filterCategory(filter.getCode(), filter.getDescription(), filter.getName(), pageable);
        log.info("Showing filtered filtered categories, total categories: '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(this::toShortResponse));

    }
}
