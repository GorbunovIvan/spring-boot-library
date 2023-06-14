package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode
@ToString(exclude = { "borrowingRecords" })
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "author_id")
    private Author author;

    private Integer year;

    @OneToMany(mappedBy = "book", cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE })
    private Set<BorrowingRecord> borrowingRecords = new LinkedHashSet<>();
}
