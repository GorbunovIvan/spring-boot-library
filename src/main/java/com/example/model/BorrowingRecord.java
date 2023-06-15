package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "history_of_borrowing")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode
public class BorrowingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "visitor_id")
    private Visitor visitor;

    @Column(name = "day_of_borrowing", nullable = false)
    private LocalDate dayOfBorrowing;

    @Column(name = "day_of_returning")
    private LocalDate dayOfReturning;

    public boolean isActive() {
        return dayOfReturning == null;
    }

    @Override
    public String toString() {
        return "Record " +
                "(id: " + id + ")" +
                " book '" + book.getName() + "'" +
                " borrowed by visitor " + visitor.getName() +
                " at " + dayOfBorrowing +
                (isActive() ? " and not returned yet" : " returned at " + dayOfReturning);
    }

    @PrePersist
    private void init() {
        if (dayOfBorrowing == null)
            dayOfBorrowing = LocalDate.now();
    }
}
