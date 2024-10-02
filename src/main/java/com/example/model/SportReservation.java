package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.time.LocalDate;

@Entity
public class SportReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long SportReservationId;
    
    @OneToOne
    private User studentId;
    private LocalDate date;
    private int startHour;
    private int endHour;
    private boolean state;

    public SportReservation(User studentId, int daysToAdd, int startHour, int endHour) {
        this.studentId = studentId;
        this.date = LocalDate.now().plusDays(daysToAdd);
        this.startHour = startHour;
        this.endHour = endHour;
        state = true;
    }

    public Long getSportReservationId() {
        return SportReservationId;
    }

    public User getStudentId() {
        return studentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    

    
    
}
