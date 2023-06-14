package com.example.repository;

import com.example.model.Book;
import com.example.model.RecordOfBorrowingHistory;
import com.example.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface RecordOfBorrowingHistoryRepository extends JpaRepository<RecordOfBorrowingHistory, Long> {

    Set<RecordOfBorrowingHistory> findAllByBook(Book book);

    Set<RecordOfBorrowingHistory> findAllByVisitor(Visitor visitor);

    @Query("FROM RecordOfBorrowingHistory WHERE dayOfReturning = null")
    Set<RecordOfBorrowingHistory> findAllActive();

    @Query("FROM RecordOfBorrowingHistory WHERE visitor = :visitor AND dayOfReturning = null")
    Set<RecordOfBorrowingHistory> findAllActiveByVisitor(@Param("visitor") Visitor visitor);
}
