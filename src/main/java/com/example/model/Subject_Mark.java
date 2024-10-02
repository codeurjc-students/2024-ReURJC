package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Subject_Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long subjectMarkId;

    @ManyToOne
    private User studentId;

    @ManyToOne
    private Subject subjectId;
    private int mark;
    private String convocatory;

    public Subject_Mark(User studentId, Subject subjectId, int mark, String convocatory) {
        this.mark = mark;
        this.studentId = studentId;
        this.convocatory = convocatory;
        this.subjectId = subjectId;
    }

    public User getStudentId() {
        return studentId;
    }

    public Subject getSubjectId() {
        return subjectId;
    }

    public int getMark() {
        return mark;
    }

    public String getConvocatory() {
        return convocatory;
    }

    
    
}
