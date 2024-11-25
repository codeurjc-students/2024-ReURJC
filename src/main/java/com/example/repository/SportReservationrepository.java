package com.example.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.SportReservation;
import com.example.model.User;

public interface SportReservationrepository extends JpaRepository<SportReservation, Long> {

    List<SportReservation> findByStudentIdOrderBySportReservationIdDesc(User user);

    @Query("SELECT sr FROM SportReservation sr WHERE sr.date <= :today")
    List<SportReservation> findByDateBeforeOrEqual(LocalDate today);
    
}
