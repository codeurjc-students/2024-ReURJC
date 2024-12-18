package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Attendance;
import com.example.model.User;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findTop10ByCreatorOrderByIdDesc(User creator);

    boolean existsByCode(String code);

    Attendance findByCode(String code);
}
