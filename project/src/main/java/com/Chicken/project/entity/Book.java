package com.Chicken.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "books")
@SQLRestriction("is_deleted = 'false'")
public class Book extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String title;
    private String author;
    private String publisher;
    private int pageCount;

    private String printType;
    private String language;

    private int quantity;

    @Column(length = 100000)
    private String description;
    @OneToMany
    private List<Article> articles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();
    private int isBorrowed;
}
