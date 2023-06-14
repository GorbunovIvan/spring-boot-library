package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "history-of-lending")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode
@ToString
public class RecordOfBorrowingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "visitor_id")
    private Visitor visitor;

    @Column(name = "day-of-taking", nullable = false)
    private LocalDate dayOfTaking;

    @Column(name = "day-of-returning")
    private LocalDate dayOfReturning;

    public boolean isActive() {
        return dayOfReturning == null;
    }
}