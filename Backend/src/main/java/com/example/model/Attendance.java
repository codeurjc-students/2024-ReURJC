package com.example.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.services.AttendanceService;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jakarta.persistence.OneToMany;

@Entity
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
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

    private static final int CODE_LENGTH = 6;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random random = new SecureRandom();

    @Autowired
    private AttendanceService attendanceService;

    public Attendance(User user, Subject subject) {
        this.creator = user;
        this.subject = subject;
        dateTime = LocalDateTime.now();
        do {
            this.code = generateRandomCode(CODE_LENGTH);
        } while (attendanceService.isCodeUsed(code));

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

    private String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    public void moreTime() {
        dateTime = LocalDateTime.now();
    }

    public User getCreator() {
        return creator;
    }

}
