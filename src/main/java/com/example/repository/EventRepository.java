package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Events.Event;

public interface EventRepository extends JpaRepository<Event,Long>{
    
}
