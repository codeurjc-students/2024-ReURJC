package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.SportReservation;
import com.example.model.User;

public interface SportReservationrepository extends JpaRepository<SportReservation, Long> {

    List<SportReservation> findByStudentIdOrderBySportReservationIdDesc(User user);
    
}
