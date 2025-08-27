package com.Chicken.project.entity.Borrow;

import com.Chicken.project.entity.BaseEntity;
import com.Chicken.project.entity.Book;
import com.Chicken.project.entity.V_User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLRestriction("is_deleted = 'false'")
public class Borrow extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private V_User user;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate borrowDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate returnDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status = BorrowStatus.NOT_RETURNED;
}
