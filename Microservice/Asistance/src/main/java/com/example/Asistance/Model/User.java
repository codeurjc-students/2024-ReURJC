package com.example.Asistance.Model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    private Long studentId;
    private String name;
    private String surname1;
    private String dni;

    public User() {
    }

    public User(Long studentId, String name, String surname1, String dni) {
        this.studentId = studentId;
        this.name = name;
        this.surname1 = surname1;
        this.dni = dni;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getSurname1() {
        return surname1;
    }

    public String getDni() {
        return dni;
    }

}
