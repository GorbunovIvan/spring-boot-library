package com.example.repository;

import com.example.model.Book;
import com.example.model.HistoryOfLending;
import com.example.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface HistoryOfLendingRepository extends JpaRepository<HistoryOfLending, Long> {

    Set<HistoryOfLending> findAllByBook(Book book);

    Set<HistoryOfLending> findAllByVisitor(Visitor visitor);

    @Query("FROM HistoryOfLending WHERE dayOfReturning = null")
    Set<HistoryOfLending> findAllActive();
}
