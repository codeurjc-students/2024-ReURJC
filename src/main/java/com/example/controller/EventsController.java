package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.model.Event;
import com.example.services.EventService;


@RestController
public class EventsController {

    @Autowired
    private EventService eventService;

    @GetMapping("/api/events")
    public ResponseEntity<List<Event>> getEvents() {

        return ResponseEntity.ok(eventService.getEvents());
    }
    
}
