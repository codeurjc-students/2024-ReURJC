package com.example.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.ManyToOne;




@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long NotificationId;

    @ManyToOne
	private User student;

    private String title;

    private String description;

    private int category;

    public Notification(User student, String title, String description, int category) {
        this.student = student;
        this.title = title;
        this.description = description;
        this.category = category;
    }

    public Notification(){}

    public Long getNotificationId() {
        return NotificationId;
    }

    public User getStudentId() {
        return student;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getCategory() {
        return category;
    }

    

}
