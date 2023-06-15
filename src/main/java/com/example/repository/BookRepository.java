package com.example.repository;

import com.example.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    @Query("FROM Book b " +
            "   LEFT JOIN FETCH b.author")
    Set<Book> findAllEagerly();

    @Query("FROM Book b " +
            "   LEFT JOIN FETCH b.borrowingRecords records " +
            "   LEFT JOIN FETCH b.author " +
            "   LEFT JOIN FETCH records.visitor " +
            "WHERE b.id = :id")
    Optional<Book> findByIdEagerly(Long id);

    Optional<Book> findByName(String name);

    @Query("FROM Book b " +
            "   LEFT JOIN FETCH b.author authors " +
            "WHERE authors.id = :authorId")
    Set<Book> findAllByAuthorId(Long authorId);
}
