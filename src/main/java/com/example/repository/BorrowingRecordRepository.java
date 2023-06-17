package com.example.repository;

import com.example.model.Book;
import com.example.model.BorrowingRecord;
import com.example.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long>, BorrowingRecordRepositoryCustom {

    @Query("FROM BorrowingRecord r " +
            "   LEFT JOIN FETCH r.visitor " +
            "   LEFT JOIN FETCH r.book book " +
            "   LEFT JOIN FETCH book.author")
    Set<BorrowingRecord> findAllEagerly();

    @Query("FROM BorrowingRecord r " +
            "   LEFT JOIN FETCH r.visitor " +
            "   LEFT JOIN FETCH r.book books " +
            "   LEFT JOIN FETCH books.author " +
            "WHERE r.book = :book")
    Set<BorrowingRecord> findAllByBook(Book book);

    @Query("FROM BorrowingRecord r " +
            "   LEFT JOIN FETCH r.visitor " +
            "   LEFT JOIN FETCH r.book books " +
            "   LEFT JOIN FETCH books.author " +
            "WHERE r.visitor = :visitor")
    Set<BorrowingRecord> findAllByVisitor(Visitor visitor);

    @Query("FROM BorrowingRecord r " +
            "   LEFT JOIN FETCH r.visitor " +
            "   LEFT JOIN FETCH r.book books " +
            "   LEFT JOIN FETCH books.author " +
            "WHERE r.dayOfReturning = null")
    Set<BorrowingRecord> findAllActive();

    @Query("FROM BorrowingRecord r " +
            "   LEFT JOIN FETCH r.visitor " +
            "   LEFT JOIN FETCH r.book books " +
            "   LEFT JOIN FETCH books.author " +
            "WHERE r.visitor = :visitor " +
            "   AND r.dayOfReturning = null")
    Set<BorrowingRecord> findAllActiveByVisitor(@Param("visitor") Visitor visitor);
}
