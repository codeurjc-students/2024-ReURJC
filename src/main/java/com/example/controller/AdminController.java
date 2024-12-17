package com.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.SportReservation;
import com.example.model.User;
import com.example.model.Events.Event;
import com.example.model.Events.VoteDelegateEvent;
import com.example.services.EventService;
import com.example.services.SportReservationService;
import com.example.services.UserService;
import com.example.services.VotesService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private SportReservationService sportReservationService;

    @Autowired
    private VotesService votesService;

    @Autowired
    private EventService eventService;

    @GetMapping("/reservations")
    public ResponseEntity<List<SportReservation>> getAllReservations(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("ADMIN")) {
                return ResponseEntity.ok(sportReservationService.getAllActiveReservations());

            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

    @GetMapping("/votes")
    public ResponseEntity<List<Object[]>> getAllVotesByEvent(HttpServletRequest request,
            @RequestParam("eventId") Long eventId) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("ADMIN")) {
                return ResponseEntity.ok(votesService.getAllVotesByIdAndEvent(eventId));

            }

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    @GetMapping("/events")
    public ResponseEntity<List<VoteDelegateEvent>> getAllEvents(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("ADMIN")) {
                return ResponseEntity.ok(eventService.getAllEvents());

            }

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/user")
    public ResponseEntity<User> getAllEvents(HttpServletRequest request, @RequestParam("userId") Long eventId) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("ADMIN")) {
                return ResponseEntity.ok(userService.findById(eventId));

            }

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
