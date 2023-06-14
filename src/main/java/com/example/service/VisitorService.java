package com.example.service;

import com.example.exception.VisitorNotFoundException;
import com.example.model.Visitor;
import com.example.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;

    public List<Visitor> findAll() {
        return visitorRepository.findAll();
    }

    public Visitor findById(Long id) {
        return visitorRepository.findById(id)
                .orElseThrow(VisitorNotFoundException::new);
    }

    public Visitor create(Visitor visitor) {
        return visitorRepository.save(visitor);
    }

    @Transactional
    public Visitor update(Long id, Visitor visitor) {
        if (!visitorRepository.existsById(id))
            throw new VisitorNotFoundException();
        visitor.setId(id);
        return visitorRepository.save(visitor);
    }

    public void deleteById(Long id) {
        visitorRepository.deleteById(id);
    }
}
