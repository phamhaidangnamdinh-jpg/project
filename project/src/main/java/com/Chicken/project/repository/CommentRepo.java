package com.Chicken.project.repository;

import com.Chicken.project.entity.Article;
import com.Chicken.project.entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {


    @Query(value = "SELECT c FROM Comment c where " +
            "c.article.id= :articleId " +
            "AND (:content IS NULL OR c.content LIKE %:content%) " +
            "AND (:author IS NULL OR c.author.username LIKE %:author%)"+
            "AND (:createDateFrom IS NULL OR c.createDate >= :createDateFrom) " +
            "AND (:createDateTo IS NULL OR c.createDate <= :createDateTo) "
    )
    Page<Comment> filterComment(@Param("articleId") long articleId,
                                @RequestParam String author,
                                @RequestParam String content,
                                @RequestParam("createDateFrom") LocalDate createDateFrom,
                                @RequestParam("createDateTo") LocalDate createDateTo,
                                Pageable pageable);

    @Query(value = """
    WITH RECURSIVE comment_tree AS (
        SELECT *
        FROM comment
        WHERE id = :rootId
        UNION ALL
        SELECT c.*
        FROM comment c
        INNER JOIN comment_tree ct ON c.parent_id = ct.id
    )
    SELECT * FROM comment_tree
    """, nativeQuery = true)
    List<Comment> findCommentTree(@Param("rootId") long rootId);


    Page<Comment> findByArticleAndParentIsNull(Article article, Pageable pageable);
    Page<Comment> findByParent(Comment parent, Pageable pageable);
    long countByParent(Comment parent);

    @Modifying
    @Query("UPDATE Comment c SET c.isDeleted = true WHERE c.id IN :ids")
    void markCommentsDeleted(@Param("ids") List<Long> ids);
}
