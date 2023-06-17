package com.example.repository;

import com.example.model.BorrowingRecord;
import org.springframework.transaction.annotation.Transactional;

public interface BorrowingRecordRepositoryCustom {

    @Transactional
    BorrowingRecord saveWithDetached(BorrowingRecord record);
}
