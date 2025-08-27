package com.Chicken.project.repository;

import com.Chicken.project.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepo extends JpaRepository<Article, Long> {
    @EntityGraph(attributePaths = {"comments"})
    Optional<Article> findWithCommentsById(int id);

    @Query(value = "SELECT a FROM Article a " +
            "WHERE (:title IS NULL OR a.title LIKE %:title%) " +
            "AND (:content IS NULL OR a.content LIKE %:content%) " +
            "AND (:createDateFrom IS NULL OR a.createDate >= :createDateFrom) " +
            "AND (:createDateTo IS NULL OR a.createDate <= :createDateTo) "+
            "AND (:author IS NULL OR a.author.username LIKE %:author%)"+
            "And (:bookName IS NULL OR a.book.title LIKE %:bookName%)")
    Page<Article> filterArticle(@RequestParam String title,
                                @RequestParam String author,
                                @RequestParam String bookName,
                                @RequestParam String content,
                                @RequestParam("createDateFrom") LocalDate createDateFrom,
                                @RequestParam("createDateTo") LocalDate createDateTo,
                                Pageable pageable);

    Page<Article> findAll(Pageable pageable);

    List<Article> findTop5ByOrderByLikeCountDesc();
}
