package com.example.Asistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Asistance.Model.UserAttendance;

public interface UserAttendanceRepository extends JpaRepository<UserAttendance, Long> {

}
