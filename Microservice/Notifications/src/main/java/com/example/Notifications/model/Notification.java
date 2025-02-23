package com.example.Notifications.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long notificationId;

    private Long student;

    private String title;

    private String description;

    public Notification(Long student, String title, String description) {
        this.student = student;
        this.title = title;
        this.description = description;
    }

    public Notification() {
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public Long getStudentId() {
        return student;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
