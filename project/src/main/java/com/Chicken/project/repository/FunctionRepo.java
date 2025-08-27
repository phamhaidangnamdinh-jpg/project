package com.Chicken.project.repository;

import com.Chicken.project.entity.Comment;
import com.Chicken.project.entity.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Repository
public interface FunctionRepo extends JpaRepository<Function, Long> {
    Boolean existsByFunctionCode(String code);
    @Query(value = "SELECT c FROM Function c where " +
            "(:functionCode IS NULL OR c.functionCode LIKE %:functionCode%) " +
            "AND (:functionName IS NULL OR c.functionName LIKE %:functionName%)"+
            "AND (:description IS NULL OR c.description LIKE %:description%) "
    )
    Page<Function> filterFunction
                                (@RequestParam(required = false) String functionCode,
                                 @RequestParam(required = false) String functionName,
                                 @RequestParam(required = false) String description,
                                 Pageable pageable);

    Page<Function> findAll(Pageable pageable);
}
