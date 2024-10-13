package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Festive;

public interface FestiveRepository extends JpaRepository<Festive,Long> {
    
}
