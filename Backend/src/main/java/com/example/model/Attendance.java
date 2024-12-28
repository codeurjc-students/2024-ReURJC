package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.OneToMany;

@Entity
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime dateTime;

    @ManyToOne
    private User creator;

    @ManyToOne
    private Subject subject;

    @Column(unique = true)
    private String code;

    @OneToMany(mappedBy = "attendance")
    private List<UserAttendance> usersPresent = new ArrayList<UserAttendance>();

    public Attendance(User user, Subject subject, String code) {
        this.creator = user;
        this.subject = subject;
        dateTime = LocalDateTime.now();
        this.code = code;

    }

    public Attendance() {
    }

    public void addUser(UserAttendance user) {
        this.usersPresent.add(user);
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Subject getSubject() {
        return subject;
    }

    public List<UserAttendance> getUsersPresent() {
        return usersPresent;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void moreTime() {
        dateTime = LocalDateTime.now();
    }

    public User getCreator() {
        return creator;
    }

    public long getId() {
        return id;
    }

}
