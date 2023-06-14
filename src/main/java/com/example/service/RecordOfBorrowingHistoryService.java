package com.example.service;

import com.example.exception.RecordOfBorrowingHistoryNotFoundException;
import com.example.model.Book;
import com.example.model.RecordOfBorrowingHistory;
import com.example.model.Visitor;
import com.example.repository.RecordOfBorrowingHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecordOfBorrowingHistoryService {

    private final RecordOfBorrowingHistoryRepository recordOfBorrowingHistoryRepository;

    public List<RecordOfBorrowingHistory> findAll() {
        return recordOfBorrowingHistoryRepository.findAll();
    }

    public RecordOfBorrowingHistory findById(Long id) {
        return recordOfBorrowingHistoryRepository.findById(id)
                .orElseThrow(RecordOfBorrowingHistoryNotFoundException::new);
    }

    public Set<RecordOfBorrowingHistory> findAllByBook(Book book) {
        return recordOfBorrowingHistoryRepository.findAllByBook(book);
    }

    public Set<RecordOfBorrowingHistory> findAllByVisitor(Visitor visitor) {
        return recordOfBorrowingHistoryRepository.findAllByVisitor(visitor);
    }

    public Set<RecordOfBorrowingHistory> findAllActive() {
        return recordOfBorrowingHistoryRepository.findAllActive();
    }

    public Set<RecordOfBorrowingHistory> findAllActiveByVisitor(Visitor visitor) {
        return recordOfBorrowingHistoryRepository.findAllActiveByVisitor(visitor);
    }

    public RecordOfBorrowingHistory create(RecordOfBorrowingHistory recordOfBorrowingHistory) {
        return recordOfBorrowingHistoryRepository.save(recordOfBorrowingHistory);
    }

    @Transactional
    public RecordOfBorrowingHistory update(Long id, RecordOfBorrowingHistory recordOfBorrowingHistory) {
        if (!recordOfBorrowingHistoryRepository.existsById(id))
            throw new RecordOfBorrowingHistoryNotFoundException();
        recordOfBorrowingHistory.setId(id);
        return recordOfBorrowingHistoryRepository.save(recordOfBorrowingHistory);
    }

    public void deleteById(Long id) {
        recordOfBorrowingHistoryRepository.deleteById(id);
    }
}
