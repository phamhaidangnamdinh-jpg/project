package com.Chicken.project.repository;

import com.Chicken.project.entity.Function;
import com.Chicken.project.entity.RoleGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface RoleGroupRepo extends JpaRepository<RoleGroup, Long> {
    RoleGroup findByRoleGroupCode(String code);
    @Query(value = "SELECT c FROM RoleGroup c where " +
            "(:roleGroupCode IS NULL OR c.roleGroupCode LIKE %:roleGroupCode%) " +
            "AND (:roleGroupName IS NULL OR c.roleGroupName LIKE %:roleGroupName%)"+
            "AND (:description IS NULL OR c.description LIKE %:description%) "
    )
    Page<RoleGroup> filterRoleGroup
            (@RequestParam(required = false) String roleGroupCode,
             @RequestParam(required = false) String roleGroupName,
             @RequestParam(required = false) String description,
             Pageable pageable);

    Page<RoleGroup> findAll(Pageable pageable);
}
