package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Notification;
import com.example.model.User;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
    List<Notification> findFirst10ByStudentOrderByNotificationIdDesc(User student); 
}
