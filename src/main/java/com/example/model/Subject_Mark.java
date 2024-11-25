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
    private String nameMark;

    public Subject_Mark(User studentId, Subject subjectId, int mark, String convocatory, String nameMark) {
        this.mark = mark;
        this.studentId = studentId;
        this.convocatory = convocatory;
        this.subjectId = subjectId;
        this.nameMark = nameMark;
    }

    public Subject_Mark() {}

    
    public Long getSubjectMarkId() {
        return subjectMarkId;
    }
    

    public void setMark(int mark) {
        this.mark = mark;
    }


    public String getNameMark() {
        return nameMark;
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
