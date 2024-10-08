package com.example.model;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Convocatoria {
    @Id@GeneratedValue(strategy = GenerationType.AUTO)
    private Long convocatoriaId;
    private String date;
    private int convocatory;
    private String classroom;

    public Convocatoria(String date, int convocatory, String classroom) {
        this.date = date;
        this.convocatory = convocatory;
        this.classroom = classroom;
    }

    public Convocatoria() {}
    
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
