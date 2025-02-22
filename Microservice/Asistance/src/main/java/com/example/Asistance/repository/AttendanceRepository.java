package com.example.Asistance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Asistance.Model.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findTop10ByCreatorOrderByIdDesc(Long creator);

    boolean existsByCode(String code);

    Attendance findByCode(String code);
}
