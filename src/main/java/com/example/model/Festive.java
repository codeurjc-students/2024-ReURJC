package com.example.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Festive {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long FestiveId;

    private int day;
    private int month;
    private int year;
    private String color;
    private int startedXDaysAgo;
    @Nullable
    private String local;


    public Festive() {}

    public Festive(int day, int month, int year, String color, int daysAgo) {
        this(day,month,year,color);
        startedXDaysAgo = daysAgo;
    }

    public Festive(int day, int month, int year, String color, String local) {
        this(day,month,year,color);
        this.local = local;
        
    }

    public Festive(int day, int month, int year, String color) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.color = color;
        startedXDaysAgo = -1;

    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getColor() {
        return color;
    }

    public int getStartedXDaysAgo() {
        return startedXDaysAgo;
    }

    public String getLocal() {
        return local;
    }

    

    

    

    
}
