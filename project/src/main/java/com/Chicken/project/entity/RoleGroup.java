package com.Chicken.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;


import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLRestriction("is_deleted = 'false'")
public class RoleGroup extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roleGroupCode;
    private String roleGroupName;
    private String description;

    @OneToMany(mappedBy = "roleGroup", cascade = {}, orphanRemoval = false)
    private Set<V_User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_group_function",
            joinColumns = @JoinColumn(name = "role_group_id"),
            inverseJoinColumns = @JoinColumn(name = "function_id")
    )
    private Set<Function> functions = new HashSet<>();

}
