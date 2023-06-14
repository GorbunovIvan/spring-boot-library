package com.example.repository;

import com.example.model.Book;
import com.example.model.BorrowingRecord;
import com.example.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

    Set<BorrowingRecord> findAllByBook(Book book);

    Set<BorrowingRecord> findAllByVisitor(Visitor visitor);

    @Query("FROM BorrowingRecord WHERE dayOfReturning = null")
    Set<BorrowingRecord> findAllActive();

    @Query("FROM BorrowingRecord WHERE visitor = :visitor AND dayOfReturning = null")
    Set<BorrowingRecord> findAllActiveByVisitor(@Param("visitor") Visitor visitor);
}
