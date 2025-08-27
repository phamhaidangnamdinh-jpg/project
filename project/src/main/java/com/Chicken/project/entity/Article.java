package com.Chicken.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLRestriction("is_deleted = 'false'")
public class Article extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private AtomicInteger likeCount = new AtomicInteger(0);
    private AtomicInteger disLikeCount = new AtomicInteger(0);
    @ManyToOne
    @JoinColumn(name = "author_id")
    private V_User author;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate updateDate;
    @ManyToOne
    private Book book;
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;


    public void incrementLike(){
        likeCount.incrementAndGet();
    }
    public void decrementLike(){
        likeCount.decrementAndGet();
    }
    public void incrementDislike(){
        disLikeCount.incrementAndGet();
    }
    public void decrementDislike(){
        disLikeCount.decrementAndGet();
    }
}
