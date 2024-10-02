package com.example.model;

import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    private Long studentId;
    
    private String name;
    private String surname1;
    private String surname2;
    private String dni;
    private int level;
    private Blob photo;
    private boolean isCandidate;
    private int votes;

    public User(int studentId, String name, String surname1, String surname2, String dni) {
        this.name= name;
        this.surname1=surname1;
        this.surname2=surname2;
        this.dni=dni;
        this.level=0;
        this.photo=null;
        votes = 0;
        isCandidate = false;
    }

    public User(int studentId, String name, String surname1, String surname2, String dni, Blob photo) {
        this(studentId, name, surname1, surname2, dni);
        this.photo=photo;
    }
    public User(int studentId, String name, String surname1, String surname2, String dni, int rank, Blob photo) {
        this(studentId, name, surname1, surname2, dni);
        this.level=rank;
        this.photo=photo;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname1() {
        return surname1;
    }

    public void setSurname1(String surname1) {
        this.surname1 = surname1;
    }

    public String getSurname2() {
        return surname2;
    }

    public void setSurname2(String surname2) {
        this.surname2 = surname2;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getRank() {
        return level;
    }

    public Blob getPhoto() {
        return photo;
    }

    public void setPhoto(Blob photo) {
        this.photo = photo;
    }

    public boolean isCandidate() {
        return isCandidate;
    }

    public void setCandidate(boolean isCandidate) {
        this.isCandidate = isCandidate;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Long getId() {
        return this.studentId;
    }

    

    
    
}
