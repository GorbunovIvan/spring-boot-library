package com.example.repository;

import com.example.model.Book;
import org.springframework.transaction.annotation.Transactional;

public interface BookRepositoryCustom {

    @Transactional
    Book saveWithDetached(Book book);
}
