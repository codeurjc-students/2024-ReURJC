package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Subject_Schedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Subject_ScheduleId;

    @ManyToOne
    private Subject subjectId;

    private int dayOfWeek;
    private int startHour;
    private int endHour;

    public Subject_Schedule(Subject subjectId, int dayOfWeek, int startHour, int endHour) {
        this.subjectId = subjectId;
        this.dayOfWeek = dayOfWeek;
        this.startHour = startHour;
        this. endHour = endHour;
    }

    public Long getSubject_ScheduleId() {
        return Subject_ScheduleId;
    }

    public Subject getSubjectId() {
        return subjectId;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    
}
