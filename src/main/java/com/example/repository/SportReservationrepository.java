package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.SportReservation;

public interface SportReservationrepository extends JpaRepository<SportReservation, Long> {
    
}
