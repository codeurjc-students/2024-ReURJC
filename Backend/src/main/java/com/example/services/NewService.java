package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.model.News;
import com.example.repository.NewsRepository;

import java.util.List;

@Service
public class NewService {

    @Autowired
    private NewsRepository repository;

    public List<News> getAllNewer(int pageNumber) {
        return repository.findAllNewer(PageRequest.of(pageNumber, 9)).getContent();
    }

}
