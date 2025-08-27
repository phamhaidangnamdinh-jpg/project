package com.Chicken.project.repository;

import com.Chicken.project.entity.V_User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepo extends JpaRepository<V_User, Long> {
    @Query("SELECT u from V_User u WHERE "+
            "lower(u.username) LIKE LOWER(CONCAT( '%', :keyword,'%')) OR "+
            "lower(u.fullname) LIKE LOWER(CONCAT( '%', :keyword,'%')) OR "+
            "lower(u.email) LIKE LOWER(CONCAT( '%', :keyword,'%')) OR "+
            "lower(u.phone) LIKE LOWER(CONCAT( '%', :keyword,'%')) OR "+
            "lower(u.identityNumber) LIKE LOWER(CONCAT( '%', :keyword,'%'))")
    List<V_User> searchUser(String keyword);


    @Query(value = "SELECT u FROM V_User u " +
            "WHERE (:username IS NULL OR u.username LIKE %:username%) " +
            "AND (:email IS NULL OR u.email LIKE %:email%) " +
            "AND (:phone IS NULL OR u.phone LIKE %:phone%)")
    Page<V_User> filterUser(@Param("username") String username,
                            @Param("email") String email,
                            @Param("phone") String phone,
                            Pageable pageable);
    @Query(value = "SELECT u FROM V_User u " +
            "WHERE (:username IS NULL OR u.username LIKE %:username%) " +
            "AND (:email IS NULL OR u.email LIKE %:email%) " +
            "AND (:phone IS NULL OR u.phone LIKE %:phone%)")
    List<V_User> filterUser(@Param("username") String username,
                            @Param("email") String email,
                            @Param("phone") String phone);


    boolean existsByUsername(String username);
    @EntityGraph(attributePaths = {"roleGroup", "roleGroup.functions"})
    V_User findByUsername(String username);
    List<V_User> findByRoleGroupId(Long roleGroupId);
    Page<V_User> findAll(Pageable pageable);
}
