package com.example.demo.service;

import com.example.demo.model.FormDetails;
import com.example.demo.repository.FormDetailsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormDetailsService {

    private final FormDetailsRepository repository;

    public FormDetailsService(FormDetailsRepository repository) {
        this.repository = repository;
    }

    public FormDetails save(FormDetails details) {
        return repository.save(details);
    }

    public List<FormDetails> findRecent(int limit) {
        var pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.findAll(pageable).getContent();
    }
}
