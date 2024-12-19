package com.example.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.SportReservation;
import com.example.model.User;

public interface SportReservationrepository extends JpaRepository<SportReservation, Long> {

        List<SportReservation> findByStudentIdOrderBySportReservationIdDesc(User user);

        @Query("SELECT sr FROM SportReservation sr WHERE sr.date BETWEEN :startOfDay AND :endOfDay AND sr.pista = :pista")
        List<SportReservation> findByDateAndPista(
                        @Param("startOfDay") LocalDateTime startOfDay,
                        @Param("endOfDay") LocalDateTime endOfDay,
                        @Param("pista") int pista);

        @Query("SELECT sr FROM SportReservation sr WHERE sr.studentId.studentId = :studentId")
        Optional<SportReservation> findByStudentId(@Param("studentId") Long studentId);

        @Query("SELECT sr FROM SportReservation sr WHERE sr.date >= :startDate")
        List<SportReservation> findByDate(
                        @Param("startDate") LocalDateTime startDate);
}
