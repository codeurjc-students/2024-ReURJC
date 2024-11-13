package com.example.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

    User findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.isCandidate = true AND u.email <> :email")
    List<User> findByIsCandidateTrueExcludingUser(@Param("email") String email);
}

    

