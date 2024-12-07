package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

}
