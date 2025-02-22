package com.example.Asistance.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Subject {

    @Id
    private Long subjectId;
    private String title;

    public Subject() {
    }

    public Subject(Long subjectId, String title) {
        this.subjectId = subjectId;
        this.title = title;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public String getTitle() {
        return title;
    }

}
