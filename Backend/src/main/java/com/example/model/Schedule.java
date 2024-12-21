package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ScheduleId;

    private int dayOfWeek;
    private int startHour;
    private int endHour;
    private String classRoom;

    @ManyToOne
    private Subject subject; // Nueva propiedad para relacionar con la clase Subject

    public Schedule(int dayOfWeek, int startHour, int endHour, String classRoom) {
        this.dayOfWeek = dayOfWeek;
        this.startHour = startHour;
        this.endHour = endHour;
        this.classRoom = classRoom;
    }

    public Schedule() {
    }

    public Long getScheduleId() {
        return ScheduleId;
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

    public String getClassRoom() {
        return classRoom;
    }
}
