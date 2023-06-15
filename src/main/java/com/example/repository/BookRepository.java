package com.example.repository;

import com.example.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    Optional<Book> findByName(String name);

    @Query("FROM Book b LEFT JOIN FETCH b.borrowingRecords WHERE b.id = :id")
    Optional<Book> findByIdEager(Long id);

    Set<Book> findAllByAuthorId(Long authorId);
}
