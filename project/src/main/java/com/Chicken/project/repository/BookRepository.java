package com.Chicken.project.repository;

import com.Chicken.project.entity.Book;
import com.Chicken.project.entity.V_User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    boolean existsByCode(String code);
    @Query(value = "SELECT b FROM Book b " +
            "WHERE lower(b.code) LIKE LOWER(CONCAT( '%', :keyword,'%')) " +
            "OR lower(b.title) LIKE LOWER(CONCAT( '%', :keyword,'%')) " +
            "OR lower(b.publisher) LIKE LOWER(CONCAT( '%', :keyword,'%')) " +
            "OR lower(b.author) LIKE LOWER(CONCAT( '%', :keyword,'%'))")
    Page<Book> filterBook(@Param("keyword") String keyword,
                            Pageable pageable);

    Page<Book> findAll(Pageable pageable);

    @Query(value = "Select category_id from book_category where book_id = :bookId", nativeQuery = true)
    List<Long> getCategoryId(@Param("bookId") Long bookId);

    @Query("""
    SELECT DISTINCT b
    FROM Book b
    LEFT JOIN b.categories c
    WHERE (:code IS NULL OR b.code LIKE %:code%)
      AND (:title IS NULL OR b.title LIKE %:title%)
      AND (:author IS NULL OR b.author LIKE %:author%)
      AND (:publisher IS NULL OR b.publisher LIKE %:publisher%)
      AND (:categoryIds IS NULL OR c.id IN :categoryIds)
    """)
    Page<Book> filterBook(@RequestParam(required = false) String code,
                          @RequestParam(required = false) String title,
                          @RequestParam(required = false) String author,
                          @RequestParam(required = false) String publisher,
                          @Param("categoryIds") List<Long> categoryIds,
                          Pageable pageable);
    @Query("""
    SELECT DISTINCT b
    FROM Book b
    LEFT JOIN b.categories c
    WHERE (:code IS NULL OR b.code LIKE %:code%)
      AND (:title IS NULL OR b.title LIKE %:title%)
      AND (:author IS NULL OR b.author LIKE %:author%)
      AND (:publisher IS NULL OR b.publisher LIKE %:publisher%)
      AND (:categoryIds IS NULL OR c.id IN :categoryIds)
    """)
    List<Book> filterBook(@RequestParam(required = false) String code,
                          @RequestParam(required = false) String title,
                          @RequestParam(required = false) String author,
                          @RequestParam(required = false) String publisher,
                          @Param("categoryIds") List<Long> categoryIds);


    @Query(value = "SELECT b FROM Book b " +
            "WHERE (:code IS NULL OR b.code LIKE %:code%) " +
            "AND (:title IS NULL OR b.title LIKE %:title%) " +
            "AND (:author IS NULL OR b.author LIKE %:author%) " +
            "AND (:author IS NULL OR b.publisher LIKE %:publisher%)")
    List<Book> filterBook(@RequestParam(required = false) String code,
                          @RequestParam(required = false) String title,
                          @RequestParam(required = false) String author,
                          @RequestParam(required = false) String publisher);
    Book findByCode(String code);
    @Query("SELECT c.name, COUNT(b) FROM Book b JOIN b.categories c GROUP BY c.name")
    List<Object[]> getBookCountByCategory();
}
