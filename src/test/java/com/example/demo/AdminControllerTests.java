package com.example.demo;



import com.example.controller.AdminController;
import com.example.model.SportReservation;
import com.example.model.User;
import com.example.model.Events.VoteDelegateEvent;
import com.example.services.EventService;
import com.example.services.SportReservationService;
import com.example.services.UserService;
import com.example.services.VotesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminControllerTests {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserService userService;

    @Mock
    private SportReservationService sportReservationService;

    @Mock
    private VotesService votesService;

    @Mock
    private EventService eventService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllReservations_adminUser_returnsReservations() {
        // Arrange
        User adminUser = new User();
        adminUser.setRoles(Arrays.asList("USER", "ADMIN")); 
        List<SportReservation> reservations = new ArrayList<>(); 

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("admin@example.com");
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(sportReservationService.getAllActiveReservations()).thenReturn(reservations);

        // Act
        ResponseEntity<List<SportReservation>> response = adminController.getAllReservations(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservations, response.getBody());
    }

    @Test
    void getAllReservations_nonAdminUser_returnsForbidden() {
        // Arrange
        User nonAdminUser = new User();
        nonAdminUser.setRoles(Arrays.asList("USER")); 

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("user@example.com");
        when(userService.findByEmail("user@example.com")).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<List<SportReservation>> response = adminController.getAllReservations(request);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getAllVotesByEvent_adminUser_returnsVotes() {
        // Arrange
        User adminUser = new User();
        adminUser.setRoles(Arrays.asList("USER", "ADMIN"));
        Long eventId = 1L;
        List<Object[]> votes = new ArrayList<>();

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("admin@example.com");
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(votesService.getAllVotesByIdAndEvent(eventId)).thenReturn(votes);

        // Act
        ResponseEntity<List<Object[]>> response = adminController.getAllVotesByEvent(request, eventId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(votes, response.getBody());
    }

    @Test
    void getAllVotesByEvent_nonAdminUser_returnsForbidden() {
        // Arrange
        User nonAdminUser = new User();
        nonAdminUser.setRoles(Arrays.asList("USER"));
        Long eventId = 1L;

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("user@example.com");
        when(userService.findByEmail("user@example.com")).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<List<Object[]>> response = adminController.getAllVotesByEvent(request, eventId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getAllEvents_adminUser_returnsEvents() {
        // Arrange
        User adminUser = new User();
        adminUser.setRoles(Arrays.asList("USER", "ADMIN"));
        List<VoteDelegateEvent> events = new ArrayList<>();

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("admin@example.com");
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(eventService.getAllEvents()).thenReturn(events);

        // Act
        ResponseEntity<List<VoteDelegateEvent>> response = adminController.getAllEvents(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(events, response.getBody());
    }

    @Test
    void getAllEvents_nonAdminUser_returnsForbidden() {
        // Arrange
        User nonAdminUser = new User();
        nonAdminUser.setRoles(Arrays.asList("USER"));

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("user@example.com");
        when(userService.findByEmail("user@example.com")).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<List<VoteDelegateEvent>> response = adminController.getAllEvents(request);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getUser_adminUser_returnsUser() {
        // Arrange
        User adminUser = new User();
        adminUser.setRoles(Arrays.asList("USER", "ADMIN"));
        Long userId = 1L;
        User retrievedUser = new User();

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("admin@example.com");
        when(userService.findByEmail("admin@example.com")).thenReturn(adminUser);
        when(userService.findById(userId)).thenReturn(retrievedUser);

        // Act
        ResponseEntity<User> response = adminController.getAllEvents(request, userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(retrievedUser, response.getBody());
    }

    @Test
    void getUser_nonAdminUser_returnsForbidden() {
        // Arrange
        User nonAdminUser = new User();
        nonAdminUser.setRoles(Arrays.asList("USER"));
        Long userId = 1L;

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("user@example.com");
        when(userService.findByEmail("user@example.com")).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<User> response = adminController.getAllEvents(request, userId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}