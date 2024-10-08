package com.example.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Subject {

    @Id
    private Long subjectId;

    private String title;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Convocatoria> convocatories = new ArrayList<> ();

    @ManyToMany(mappedBy = "subjects")
    private Collection<User> user = new ArrayList<User>();

    public Subject() {}
    public Subject(Long i, String string) {
        this.subjectId = i;
        this.title = string;
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return this.subjectId;
    }
    public List<Convocatoria> getConvocatories() {
        return convocatories;
    }

    


    
}


