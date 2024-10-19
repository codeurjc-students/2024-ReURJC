package com.example.services;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.News;
import com.example.repository.NewsRepository;

import java.util.List;

@Service
public class NewService {

    @Autowired
    private NewsRepository repository;


    public List<News> getAll(int pageNumber) {
        return repository.findAll(new Pageable(pageNumber, 5, null)).getContent();
    }
    
}
