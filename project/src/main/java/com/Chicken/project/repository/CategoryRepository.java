package com.Chicken.project.repository;

import com.Chicken.project.entity.Article;
import com.Chicken.project.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCodeIn(List<String> codes);
    boolean existsByCode(String code);
    Category findByCode(String code);
    Page<Category> findAll(Pageable pageable);

    @Query(value = "Select book_id from book_category where category_id = :categoryId", nativeQuery = true)
    List<Long> getBookId(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT a FROM Category a " +
            "WHERE (:code IS NULL OR a.code LIKE %:code%) " +
            "AND a.isDeleted = false "+
            "AND (:description IS NULL OR a.description LIKE %:description%) " +
            "AND (:name IS NULL OR a.name >= :name)")
    Page<Category> filterCategory(@RequestParam(required = false) String code,
                                 @RequestParam(required = false) String description,
                                 @RequestParam(required = false) String name,
                                 Pageable pageable);
}
