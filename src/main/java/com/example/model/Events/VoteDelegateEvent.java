package com.example.model.Events;

import com.example.model.Events.interfaces.VoteDelegates;

import jakarta.persistence.Entity;

@Entity
public class VoteDelegateEvent extends Event implements VoteDelegates {

    public VoteDelegateEvent(String category, String title, String description , String endDate, int tabDisplay){
        super(category, title, description, "/voteDelegate", endDate);
    }

    public VoteDelegateEvent(String category, String title, String description , String endDate) {
        this(category, title, description, endDate, 1);
    }

    public VoteDelegateEvent() {}
    
}
