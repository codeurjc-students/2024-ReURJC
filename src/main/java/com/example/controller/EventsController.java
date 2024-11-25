package com.example.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.controller.Responses.VoteInfo;
import com.example.model.User;
import com.example.model.Events.Event;
import com.example.services.EventService;
import com.example.services.UserService;
import com.example.services.VotesService;
import com.example.services.securityServices.jwt.AuthResponse;
import com.example.services.securityServices.jwt.AuthResponse.Status;
import com.example.services.securityServices.jwt.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;


@RestController
public class EventsController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private VotesService voteService;

    @GetMapping("/api/events")
    public ResponseEntity<List<Event>> getEvents() {

        return ResponseEntity.ok(eventService.getEvents());
    }

    @GetMapping("/api/events/isDelegateActivated")
    public ResponseEntity<Boolean> isCandidateEvent() {

        return ResponseEntity.ok(eventService.isBecomeCandidateEvent());
    }

    @GetMapping("/api/events/isVoteDelegateActivated")
    public ResponseEntity<Boolean> isVoteDelegateEvent() {

        return ResponseEntity.ok(eventService.isVoteDelegatesEvent());
    }

    @PostMapping("/api/events/vote")
	public ResponseEntity<Boolean> vote(HttpServletRequest request, @RequestBody long candidateId) {
		Principal principal = request.getUserPrincipal();
        if (eventService.isVoteDelegatesEvent()) {
            if (principal != null) {
                if (userService.findById(candidateId).isCandidate()) {
                    User user = userService.findByEmail(principal.getName());
                    if (!voteService.hasUserAlreadyVoted(user,eventService.getVoteDelegatesEvent().getEventId())) {
                        voteService.createVote(user, candidateId, eventService.getVoteDelegatesEvent().getEventId());
                        return ResponseEntity.ok(true);
                    }
                }
            }
        }

        return ResponseEntity.notFound().build();	

    
}

@Scheduled(cron = "0 0 0 * * ?")
    public void scheduleVotingEnd() {
        
        if ( !eventService.isVoteDelegatesEvent())
        eventService.endVotingEvent();
    }
}
