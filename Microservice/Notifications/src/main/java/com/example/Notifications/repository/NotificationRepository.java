package com.example.Notifications.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Notifications.model.Notification;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findFirst10ByStudentOrderByNotificationIdDesc(Long student);
}
