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
public class Function extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String functionCode;
    private String functionName;
    private String description;
    @ManyToMany(mappedBy = "functions")
    private Set<RoleGroup> roleGroups = new HashSet<>();
}
