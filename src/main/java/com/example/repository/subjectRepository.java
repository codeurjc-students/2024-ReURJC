package com.example.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    
}
