package com.example.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Subject;
import com.example.repository.SubjectRepository;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public Subject getSubject(long id) {
        return subjectRepository.findById(id).get();
    }

    

    
}
