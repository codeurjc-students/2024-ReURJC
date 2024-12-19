package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.UserAttendance;

public interface UserAttendanceRepository extends JpaRepository<UserAttendance, Long> {

}
