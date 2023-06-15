package com.example.service;

import com.example.exception.BorrowingRecordNotFoundException;
import com.example.model.Book;
import com.example.model.BorrowingRecord;
import com.example.model.Visitor;
import com.example.repository.BorrowingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BorrowingRecordService {

    private final BorrowingRecordRepository borrowingRecordRepository;

    public BorrowingRecord findById(Long id) {
        return borrowingRecordRepository.findById(id)
                .orElseThrow(BorrowingRecordNotFoundException::new);
    }

    public Set<BorrowingRecord> findAll() {
        return borrowingRecordRepository.findAllEagerly();
    }

    public Set<BorrowingRecord> findAllByBook(Book book) {
        return borrowingRecordRepository.findAllByBook(book);
    }

    public Set<BorrowingRecord> findAllByVisitor(Visitor visitor) {
        return borrowingRecordRepository.findAllByVisitor(visitor);
    }

    public Set<BorrowingRecord> findAllActive() {
        return borrowingRecordRepository.findAllActive();
    }

    public Set<BorrowingRecord> findAllActiveByVisitor(Visitor visitor) {
        return borrowingRecordRepository.findAllActiveByVisitor(visitor);
    }

    public BorrowingRecord create(BorrowingRecord BorrowingRecord) {
        return borrowingRecordRepository.save(BorrowingRecord);
    }

    @Transactional
    public BorrowingRecord update(Long id, BorrowingRecord BorrowingRecord) {
        if (!borrowingRecordRepository.existsById(id))
            throw new BorrowingRecordNotFoundException();
        BorrowingRecord.setId(id);
        return borrowingRecordRepository.save(BorrowingRecord);
    }

    public void deleteById(Long id) {
        borrowingRecordRepository.deleteById(id);
    }
}
