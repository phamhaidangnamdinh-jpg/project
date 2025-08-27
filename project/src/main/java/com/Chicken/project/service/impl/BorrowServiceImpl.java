package com.Chicken.project.service.impl;


import com.Chicken.project.dto.request.Borrow.BorrowCreateRequest;
import com.Chicken.project.dto.request.Borrow.BorrowFilterRequest;
import com.Chicken.project.dto.response.Borrow.BorrowResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.entity.Book;
import com.Chicken.project.entity.Borrow.Borrow;
import com.Chicken.project.entity.Borrow.BorrowStatus;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.exception.BusinessException;
import com.Chicken.project.repository.BookRepository;
import com.Chicken.project.repository.BorrowRepo;
import com.Chicken.project.repository.UserRepo;
import com.Chicken.project.utils.PageResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Service
public class BorrowServiceImpl {
    @Autowired
    BorrowRepo repo;
    @Autowired
    UserRepo uRepo;
    @Autowired
    BookRepository bRepo;
    private static final Logger log =  LoggerFactory.getLogger(BookServiceImpl.class);
    public BorrowResponse toResponse(Borrow borrow){
        log.info("Converting response id '{}' into response", borrow.getId());
        BorrowResponse br = new BorrowResponse();
        br.setBorrowDate(borrow.getBorrowDate());
        br.setReturnDate(borrow.getReturnDate());
        br.setUserName(borrow.getUser().getUsername());
        br.setBookTitle(borrow.getBook().getTitle());
        br.setStatus(borrow.getStatus());
        return br;
    }
    public BorrowResponse createBorrow(BorrowCreateRequest req){
        log.info("Creating new borrow record");
        if(!uRepo.existsById(req.getUser_id())){
            log.warn("User id '{}' doesn't exist", req.getUser_id());
            throw new BusinessException("error.user.notFound");
        }
        if(!bRepo.existsById(req.getBook_id())){
            log.warn("Book id '{}' doesn't exist", req.getBook_id());
            throw new BusinessException("error.book.notFound");
        }
        Borrow b = new Borrow();
        b.setUser(uRepo.findById(req.getUser_id()).get());
        Book book1 = bRepo.findById(req.getBook_id()).get();
        if(book1.getIsBorrowed()>=book1.getQuantity())  throw new BusinessException("error.book.notAvailable");
        book1.setIsBorrowed(book1.getIsBorrowed()+1);
        bRepo.save(book1);
        b.setBook(book1);

        b.setBorrowDate(LocalDate.now());
        b.setReturnDate(req.getReturnDate());
        log.info("Created new borrow record");
        return toResponse(repo.save(b));
    }

    public PageResponse<BorrowResponse> viewBorrow(V_User user, int page, int size){
        log.info("Showing list of borrow records");
//        boolean isAdmin = user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
//        boolean isLibrarian = user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("LIBRARIAN"));
        Pageable pageable = PageRequest.of(page, size);
        boolean isAdmin = user.getRoleGroup().getRoleGroupCode().equals("ADMIN");
        boolean isLibrarian = user.getRoleGroup().getRoleGroupCode().equals("LIBRARIAN");
        if(isAdmin || isLibrarian) {
            log.info("Showing all borrow records");
            return PageResponseUtil.fromPage(repo.findBorrowHistory(pageable).map(this::toResponse));
        }
        log.info("Showing user '{}' borrow records", user.getUsername());
        return PageResponseUtil.fromPage(repo.findBorrowHistoryByUserOrderByReturnedStatus(user.getId(), pageable).map(this::toResponse));
    }
    public BorrowResponse viewBorrowDetail(V_User user, long id){
        log.info("Received request to show detail of borrow record id '{}'", id);
        if(!repo.existsById(id)) {
            log.warn("Borrow records id '{}' does not exist", id);
            throw new BusinessException("error.borrow.notFound");
        }
        boolean isAdmin = user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        boolean isLibrarian = user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("LIBRARIAN"));
        if(isAdmin || isLibrarian) return toResponse(repo.findById(id).get());
        if(repo.findBorrowByIdAndUserId(id, user.getId()) == null) {
            log.warn("User does not have borrow record id '{}'", id);
            throw new BusinessException("error.borrow.notFound");
        }
        return toResponse(repo.findBorrowByIdAndUserId(id, user.getId()));
    }

    public BorrowResponse returnBook(long id){
        log.info("Received request to return book for borrow records id '{}'", id);
        Borrow b = repo.findById(id).get();
        Book book1 = b.getBook();
        book1.setIsBorrowed(book1.getIsBorrowed()-1);
        bRepo.save(book1);
        b.setStatus(BorrowStatus.RETURNED);
        return toResponse(repo.save(b));
    }
    public Boolean deleteBorrow(long id){
        log.info("Received request to delete borrow record id '{}'", id);
        if(!repo.existsById(id)){
            log.warn("Borrow record id '{}' doesn't exist", id);
            throw new BusinessException("error.borrow.notFound");
        }
        Borrow b= repo.findById(id).get();
        Book book = b.getBook();
        book.setIsBorrowed(book.getIsBorrowed()-1);
        bRepo.save(book);
        b.setDeleted(true);
        log.info("Deleted borrow record id '{}'", id);
//        repo.deleteById(id);
        return true;
    }
    public PageResponse<BorrowResponse> filter(V_User currentUser,
                                               @RequestParam(required = false) String user,
                                               @RequestParam(required = false) String bookName,
                                               @RequestParam(required = false) String bookCode,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate borrowDateMin,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate borrowDateMax,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate returnDateMin,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate returnDateMax,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size){
        log.info("Received request to view filtered borrow records");
        boolean isAdmin = "ADMIN".equals(currentUser.getRoleGroup().getRoleGroupCode());
        boolean isLibrarian = "LIBRARIAN".equals(currentUser.getRoleGroup().getRoleGroupCode());
        if (!isAdmin && !isLibrarian) {
            log.info("User '{}' is not admin/librarian → restricting results to their own records", currentUser.getUsername());
            user = currentUser.getUsername();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Borrow> result = repo.filterBorrow(user, bookName, bookCode, borrowDateMin, borrowDateMax, returnDateMin, returnDateMax, pageable);
        log.info("Showing filtered borrow records, total records: '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(this::toResponse));
    }
    public PageResponse<BorrowResponse> search(
            V_User currentUser, @RequestBody BorrowFilterRequest filter,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size){
        log.info("Received request to view filtered borrow records");
        boolean isAdmin = "ADMIN".equals(currentUser.getRoleGroup().getRoleGroupCode());
        boolean isLibrarian = "LIBRARIAN".equals(currentUser.getRoleGroup().getRoleGroupCode());
        if (!isAdmin && !isLibrarian) {
            log.info("User '{}' is not admin/librarian → restricting results to their own records", currentUser.getUsername());
            filter.setUser(currentUser.getUsername());
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Borrow> result = repo.filterBorrow(filter.getUser(), filter.getBookName(), filter.getBookCode(), filter.getBorrowDateMin(), filter.getBorrowDateMax(), filter.getReturnDateMin(), filter.getReturnDateMax(), pageable);
        log.info("Showing filtered borrow records, total records: '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(this::toResponse));
    }
}
