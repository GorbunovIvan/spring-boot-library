package com.example.service;

import com.example.exception.AuthorNotFoundException;
import com.example.model.Author;
import com.example.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(AuthorNotFoundException::new);
    }

    public Author findByName(String name) {
        return authorRepository.findByName(name)
                .orElseThrow(AuthorNotFoundException::new);
    }

    public boolean existsByName(String name) {
        return authorRepository.existsByName(name);
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author create(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    public Author update(Long id, Author author) {
        if (!authorRepository.existsById(id))
            throw new AuthorNotFoundException();
        author.setId(id);
        return authorRepository.save(author);
    }

    public void deleteById(Long id) {
        authorRepository.deleteById(id);
    }
}
