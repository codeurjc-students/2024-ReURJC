package com.example.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Subject {

    @Id
    private Long subjectId;
    private String title;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Convocatory> convocatories = new ArrayList<>();

    @ManyToMany(mappedBy = "subjects")
    private Collection<User> user = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Schedule> schedule = new ArrayList<>();

    public Subject() {
    }

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

    public List<Convocatory> getConvocatories() {
        return convocatories;
    }

    public List<Schedule> getSchedule() {
        return schedule;
    }
}
