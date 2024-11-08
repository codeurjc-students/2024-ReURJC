package com.example.services;

import java.util.List;
import java.util.stream.Collectors;
import com.example.model.Event;
import com.example.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> getEvents() {
        return eventRepository.findAll().stream()
                .filter(event -> event.isValid())
                .collect(Collectors.toList());
    }

    public void save(Event event) {
        this.eventRepository.save(event);
    }
}

