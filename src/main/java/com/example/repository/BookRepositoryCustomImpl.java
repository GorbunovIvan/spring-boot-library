package com.example.repository;

import com.example.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Book saveWithDetached(Book book) {
        return entityManager.merge(book);
    }
}
