package com.example.demo;

import com.example.controller.EventsController;
import com.example.model.Events.BecomeCandidateEvent;
import com.example.model.Events.Event;
import com.example.model.Events.VoteDelegateEvent;
import com.example.model.User;
import com.example.services.EventService;
import com.example.services.UserService;
import com.example.services.VotesService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventsControllerTests {

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private VotesService voteService;

    @InjectMocks
    private EventsController eventsController;

    @Test
    void testGetEvents() {
    
        List<Event> events = new ArrayList<>();
        events.add(new BecomeCandidateEvent());
        when(eventService.getEvents()).thenReturn(events);

        ResponseEntity<List<Event>> response = eventsController.getEvents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(events, response.getBody());
    }

    @Test
    void testIsCandidateEvent() {
        when(eventService.isBecomeCandidateEvent()).thenReturn(true);

        ResponseEntity<Boolean> response = eventsController.isCandidateEvent();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    void testIsVoteDelegateEvent() {
        when(eventService.isVoteDelegatesEvent()).thenReturn(false);

        ResponseEntity<Boolean> response = eventsController.isVoteDelegateEvent();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody());
    }

    @Test
    void testVote_whenUserIsAuthenticatedAndEventIsActive() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User(1L, "John", "Doe", "Smith", "12345678A", "mariscalalonso16@icloud.com",
        "123");
        VoteDelegateEvent voteDelegateEvent = new VoteDelegateEvent();

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(eventService.isVoteDelegatesEvent()).thenReturn(true);
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        User user2 = new User(2L, "John", "Doe", "Smith", "12345678A", "mariscalalonso16@icloud.com",
        "123");
        when(userService.findById(2L)).thenReturn(user2);
        user2.setCandidate(true);
        when(eventService.getVoteDelegatesEvent()).thenReturn(voteDelegateEvent);
        when(voteService.hasUserAlreadyVoted(user, voteDelegateEvent.getEventId())).thenReturn(false);

        ResponseEntity<String> response = eventsController.vote(request, 2L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testVote_whenUserIsAuthenticatedAndEventIsNotActive() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);

        when(request.getUserPrincipal()).thenReturn(principal);
        when(eventService.isVoteDelegatesEvent()).thenReturn(false); 

        ResponseEntity<String> response = eventsController.vote(request, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testVote_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null);

        ResponseEntity<String> response = eventsController.vote(request, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}