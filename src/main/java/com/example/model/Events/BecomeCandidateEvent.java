package com.example.model.Events;

import com.example.model.Events.interfaces.BecomeDelegateEvent;

import jakarta.persistence.Entity;

@Entity
public class BecomeCandidateEvent extends Event implements BecomeDelegateEvent {

    public BecomeCandidateEvent(String category, String title, String description, String endDate, int tabDisplay) {
        super(category, title, description, "/becomeDelegate", endDate);
    }

    public BecomeCandidateEvent(String category, String title, String description, String endDate) {
        super(category, title, description, "/becomeDelegate", endDate, 1);
    }

    public BecomeCandidateEvent() {
    }

}
