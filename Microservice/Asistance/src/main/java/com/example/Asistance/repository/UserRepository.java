package com.example.Asistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Asistance.Model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
