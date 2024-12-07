package com.example.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Festive;
import com.example.repository.FestiveRepository;

@Service
public class FestiveService {

    @Autowired
    private FestiveRepository repository;

    public List<Festive> getAll() {
        return repository.findAll();
    }

}
