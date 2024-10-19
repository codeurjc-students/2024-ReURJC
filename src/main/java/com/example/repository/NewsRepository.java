package com.example.repository;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.News;

public interface NewsRepository extends JpaRepository<News, Long>{

    Page<News> findAll(Pageable pageable);
    
}
