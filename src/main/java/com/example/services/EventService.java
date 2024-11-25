package com.example.services;

import java.util.List;
import java.util.stream.Collectors;

import com.example.model.User;
import com.example.model.Events.Event;
import com.example.model.Events.VoteDelegateEvent;
import com.example.model.Events.interfaces.BecomeDelegateEvent;
import com.example.model.Events.interfaces.VoteDelegates;
import com.example.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserService userService;

    public List<Event> getEvents() {
        return eventRepository.findAll().stream()
                .filter(event -> event.isValid())
                .collect(Collectors.toList());
    }

    public void save(Event event) {
        this.eventRepository.save(event);
    }

    public boolean isBecomeCandidateEvent() {
        
        for (Event event : getEvents()) {
            if (event instanceof BecomeDelegateEvent) {
                return true;
            }
            
        }
        return false;
        
    }

    public boolean isVoteDelegatesEvent() {
        
        for (Event event : getEvents()) {
            if (event instanceof VoteDelegates) {
                return true;
            }
            
        }
        return false;
        
    }

    public Event getVoteDelegatesEvent() {
        for (Event event : getEvents()) {
            if (event instanceof VoteDelegateEvent) {
                return event;
            }
            
        }
        return null;
    }

    public void endVotingEvent() {
        for (User user : userService.getAll()) {
            user.setCandidate(false);
            userService.save(user);
        }
    }
}

