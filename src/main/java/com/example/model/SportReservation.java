package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class SportReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sportReservationId;
    
    @ManyToOne
    private User studentId;
    private LocalDateTime date;
    private int pista;

    public SportReservation(User studentId, LocalDateTime day, int pista) {
        this.studentId = studentId;
        this.date = day;
        this.pista = pista;
    }

    public SportReservation() {}

    public Long getSportReservationId() {
        return sportReservationId;
    }

    public User getStudentId() {
        return studentId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getPista() {
        return pista;
    }

    
    

    

    
    
}
