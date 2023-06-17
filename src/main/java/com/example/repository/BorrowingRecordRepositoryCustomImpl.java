package com.example.repository;

import com.example.model.BorrowingRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class BorrowingRecordRepositoryCustomImpl implements BorrowingRecordRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BorrowingRecord saveWithDetached(BorrowingRecord record) {
        return entityManager.merge(record);
    }
}
