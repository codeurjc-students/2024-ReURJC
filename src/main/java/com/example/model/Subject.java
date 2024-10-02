package com.example.model;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Scalar.String;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Subject {

    @Id
    private Long subjectId;

    private String title;

    public Subject(Long subjectId, String title) {
        this.subjectId = subjectId;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return this.subjectId;
    }

    

    
}
