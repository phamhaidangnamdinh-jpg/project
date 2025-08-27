package com.Chicken.project.repository;

import com.Chicken.project.entity.Article;
import com.Chicken.project.entity.Borrow.Borrow;
import com.Chicken.project.entity.Borrow.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRepo extends JpaRepository<Borrow, Long> {
    @Query("SELECT b FROM Borrow b " +
            "ORDER BY " +
            "CASE b.status " +
            "WHEN 'NOT_RETURNED' THEN 0 " +
            "WHEN 'LATE' THEN 1 " +
            "WHEN 'RETURNED' THEN 2 " +
            "END, b.borrowDate DESC")
    Page<Borrow> findBorrowHistory(Pageable pageable);
    @Query("SELECT b FROM Borrow b " +
            "WHERE b.user.id = :userId " +
            "ORDER BY " +
            "CASE b.status " +
            "WHEN 'NOT_RETURNED' THEN 0 " +
            "WHEN 'LATE' THEN 1 " +
            "WHEN 'RETURNED' THEN 2 " +
            "END, b.borrowDate DESC")
    Page<Borrow> findBorrowHistoryByUserOrderByReturnedStatus(@Param("userId") Long userId, Pageable pageable);
    @Query("SELECT b FROM Borrow b WHERE b.id = :borrowId AND b.user.id = :userId")
    Borrow findBorrowByIdAndUserId(@Param("borrowId") Long borrowId, @Param("userId") Long userId);




    List<Borrow> findByStatusNot(BorrowStatus status);

    @Query(value = "SELECT a FROM Borrow a " +
            "WHERE (:user IS NULL OR a.user.username LIKE %:user%) " +
            "AND (:bookName IS NULL OR a.book.title LIKE %:bookName%) " +
            "AND (:bookCode IS NULL OR a.book.code LIKE %:bookCode%) " +
            "AND (:borrowDateMin IS NULL OR a.borrowDate >= :borrowDateMin) "+
            "AND (:borrowDateMax IS NULL OR a.borrowDate <= :borrowDateMax) "+
            "AND (:returnDateMin IS NULL OR a.returnDate >= :returnDateMin) "+
            "AND (:returnDateMax IS NULL OR a.returnDate <= :returnDateMax) "
            )
    Page<Borrow> filterBorrow(@RequestParam(required = false) String user,
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
                                Pageable pageable);


}
