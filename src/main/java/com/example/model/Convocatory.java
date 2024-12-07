package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Convocatory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long convocatoriaId;
    private String date;
    private int convocatory;
    private String classroom;

    public Convocatory(String date, int convocatory, String classroom) {
        this.date = date;
        this.convocatory = convocatory;
        this.classroom = classroom;
    }

    public Convocatory() {
    }

    public Long getConvocatoriaId() {
        return convocatoriaId;
    }

    public String getDate() {
        return date;
    }

    public int getConvocatory() {
        return convocatory;
    }

    public String getClassroom() {
        return classroom;
    }

}
