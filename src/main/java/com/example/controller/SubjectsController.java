package com.example.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.repository.SubjectRepository;

@RestController
public class SubjectsController {
    private final SubjectRepository repository;

    SubjectsController(SubjectRepository repository) {

        this.repository = repository;
    }
    
}
