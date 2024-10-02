package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
    
}
