package com.example.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Event {
    @Id@GeneratedValue(strategy = GenerationType.AUTO)
    private Long EventId;
    private String subtitle;
    private String title;
    private String description;
    private String apiCaller;
    @JsonIgnore
    private String endDate;
    private int tabDisplay;

    public Event() {}
    public Event(String category, String title, String description, String apiCaller, String endDate, int tabDisplay) {
        subtitle = category;
        this.title = title;
        this.description = description;
        this.apiCaller = apiCaller;
        this.endDate = endDate;
        this.tabDisplay = tabDisplay;
    }
    public Event(String category, String title, String description, String apiCaller, String endDate) {
        this(category, title, description, apiCaller, endDate, 1);
    }

    // Getters
    public String getSubtitle() {
        return subtitle;
    }

    public String getApiCaller() {
        return apiCaller;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getEndDate() {
        return endDate;
    }

    private LocalDate getDateInLocalDate() {
        return LocalDate.parse(this.endDate);
    }

    public boolean isValid(){
        return getDateInLocalDate().isAfter(LocalDate.now());
    }
    public int getTabsDisplay() {
        return tabDisplay;
    }

    

    
}

