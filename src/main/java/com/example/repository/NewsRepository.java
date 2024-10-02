package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.News;

public interface NewsRepository extends JpaRepository<News, Long>{
    
}
