package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.example.controller.UserController;
import com.example.model.Attendance;
import com.example.model.SportReservation;
import com.example.model.Subject;
import com.example.model.Subject_Mark;
import com.example.model.User;
import com.example.model.Events.Event;
import com.example.model.Events.VoteDelegateEvent;
import com.example.services.EventService;
import com.example.services.SportReservationService;
import com.example.services.SubjectMarkService;
import com.example.services.UserService;
import com.example.services.VotesService;
import com.example.services.securityServices.jwt.AuthResponse;
import com.example.services.securityServices.jwt.LoginRequest;
import com.example.services.securityServices.jwt.UserLoginService;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
public class UserControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private VotesService voteService;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserLoginService userLoginService;

    @Mock
    private EventService eventService;

    @Mock
    private SubjectMarkService subjectMarkService;

    @Mock
    private SportReservationService sportReservationService;

    @Test
    void testSubjectsWithValidUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("mariscalalonso16@icloud.com");

        // Simula el User
        User user = new User(1L, "John", "Doe", "Smith", "12345678A", "mariscalalonso16@icloud.com",
                "123");
        Subject subject1 = new Subject(10L, "Historia");
        Subject subject2 = new Subject(23L, "Matemáticas");
        user.getSubjects().addAll(List.of(subject1, subject2));

        when(userService.findByEmail("mariscalalonso16@icloud.com")).thenReturn(user);

        // Llama al método subjects
        ResponseEntity<?> response = userController.subjects(request);

        // Verifica la respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getSubjects(), response.getBody());

    }

    @Test
    void testSubjectWithInvalidUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = null;
        when(request.getUserPrincipal()).thenReturn(principal);

        ResponseEntity<?> response = userController.subjects(request);

        // Verifica la respuesta
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    void testSetDeviceToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("mariscalalonso16@icloud.com");

        // Simula el User
        User user = new User(1L, "John", "Doe", "Smith", "12345678A", "mariscalalonso16@icloud.com",
                "123");
        when(userService.findByEmail("mariscalalonso16@icloud.com")).thenReturn(user);
        doAnswer(invocation -> {
            user.addFcmToken("123");
            return null; // setToken es void, por lo que se devuelve null
        }).when(userService).setToken(user, "123");

        ResponseEntity<?> response = userController.setDeviceToken(request, "123");

        // Verifica la respuesta
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("123", user.getFcmToken().get(0));

    }

    @Test
    void testsetFcmTokenWithInvalidUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = null;
        when(request.getUserPrincipal()).thenReturn(principal);

        ResponseEntity<?> response = userController.setDeviceToken(request, "123");

        // Verifica la respuesta
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

    }

    @Test
    void testLoginWithExistingUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");

        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado
        when(userService.existsByEmail("test@example.com")).thenReturn(true); // Usuario existe
        when(userLoginService.login(loginRequest, null, null))
                .thenReturn(ResponseEntity.ok(new AuthResponse())); // Login exitoso

        ResponseEntity<AuthResponse> response = userController.login(null, null, request, loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode()); // Verifica el código de estado

    }

    @Test
    void testLoginWithNonExistingUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");

        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado
        when(userService.existsByEmail("test@example.com")).thenReturn(false); // Usuario no existe

        ResponseEntity<AuthResponse> response = userController.login(null, null, request, loginRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // Verifica el código de estado
    }

    @Test
    void testGetMeIsDelegate_whenUserIsDelegate() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        user.setCandidate(true); // Usuario es delegado

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<Boolean> response = userController.getMeIsDelegate(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody()); // Verifica que la respuesta es true
    }

    @Test
    void testGetMeIsDelegate_whenUserIsNotDelegate() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        user.setCandidate(false); // Usuario no es delegado

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<Boolean> response = userController.getMeIsDelegate(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody()); // Verifica que la respuesta es false
    }

    @Test
    void testGetMeIsDelegate_whenUserIsNotAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<Boolean> response = userController.getMeIsDelegate(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // Verifica que la respuesta es NOT_FOUND
    }

    @Test
    void testCancelCandidacy_whenUserIsAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User(1L, "John", "Doe", "Smith", "12345678A", "mariscalalonso16@icloud.com",
                "123");
        user.setCandidate(true); // El usuario es inicialmente candidato

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(request.getRequestURI()).thenReturn("/api/v1/users/me/cancelCandidacy"); // Simula la URI

        ResponseEntity<URI> response = userController.cancelCandidacy(request);

        assertEquals(HttpStatus.OK, response.getStatusCode()); // Verifica el código de estado
        assertEquals(URI.create("/api/v1/users/me/cancelCandidacy/1"), response.getBody()); // Verifica la URI
        verify(userService).save(user); // Verifica que se llama a userService.save
        assertEquals(false, user.isCandidate()); // Verifica que isCandidate es false
    }

    @Test
    void testCancelCandidacy_whenUserIsNotAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<URI> response = userController.cancelCandidacy(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()); // Verifica el código de estado
    }

    @Test
    void testGetCandidates_whenUserIsAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        List<User> candidates = Collections.singletonList(new User()); // Lista de candidatos

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.getCandidatesExcludingUser("test@example.com")).thenReturn(candidates);

        ResponseEntity<List<User>> response = userController.getCandidates(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidates, response.getBody()); // Verifica la lista de candidatos
    }

    @Test
    void testGetCandidates_whenUserIsNotAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<List<User>> response = userController.getCandidates(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testHasVoted_whenUserHasVoted() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        Event voteDelegateEvent = new VoteDelegateEvent();

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(voteService.hasUserAlreadyVoted(user, 1L)).thenReturn(true); // Simula que el usuario ha votado
        when(eventService.getVoteDelegatesEvent()).thenReturn(voteDelegateEvent); // Simula el evento

        ResponseEntity<Boolean> response = userController.hasVoted(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody());
    }

    @Test
    void testGetGrades_whenUserIsAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        List<Subject_Mark> grades = Collections.singletonList(new Subject_Mark()); // Lista de notas

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(subjectMarkService.findSubjectsByStudent(user)).thenReturn(grades);

        ResponseEntity<List<Subject_Mark>> response = userController.getGrades(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grades, response.getBody());
    }

    // ... (Añade tests similares para cuando el usuario no está autenticado)

    @Test
    void testNewSportReservation_whenUserIsAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        Map<String, Object> reservationInfo = new HashMap<>();
        reservationInfo.put("año", "2024");
        reservationInfo.put("mes", "12");
        reservationInfo.put("fecha", "11");
        reservationInfo.put("hora", "10");
        reservationInfo.put("pista", 1);

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(sportReservationService.newReserve(user, LocalDateTime.of(2024, 12, 11, 10, 0), 1))
                .thenReturn(1L); // Simula el ID de la reserva
        when(request.getRequestURI()).thenReturn("/api/v1/users/newReservation"); // Simula la URI

        ResponseEntity<URI> response = userController.newSportReservation(request, reservationInfo);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    // ... (Añade tests similares para cuando el usuario no está autenticado y para
    // casos de error en el formato de la fecha)

    @Test
    void testGetReservations() {
        int pista = 1;
        int año = 2024;
        int mes = 12;
        int dia = 11;
        LocalDate date = LocalDate.of(año, mes, dia);
        List<SportReservation> reservations = Collections.singletonList(new SportReservation()); // Lista de reservas

        when(sportReservationService.getActivereservations(pista, date)).thenReturn(reservations);

        ResponseEntity<List<SportReservation>> response = userController.getReservations(pista, año, mes, dia);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservations, response.getBody());
    }

    @Test
    void testHasReservation_whenUserHasReservation() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(sportReservationService.isreserveActive(user)).thenReturn(true); // Simula que el usuario tiene reserva

        ResponseEntity<Boolean> response = userController.hasReservation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    void testDeletereservation_whenUserIsAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<?> response = userController.deletereservation(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(sportReservationService).deleteReservation(user); // Verifica que se llama a deleteReservation
    }

    @Test
    void testGetUserReservation_whenUserIsAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        SportReservation reservation = new SportReservation();

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(sportReservationService.getUserReserve(user)).thenReturn(reservation);

        ResponseEntity<SportReservation> response = userController.getUserReservation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservation, response.getBody());
    }

    @Test
    void testHasVoted_whenUserIsNotAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<Boolean> response = userController.hasVoted(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetGrades_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<List<Subject_Mark>> response = userController.getGrades(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testNewSportReservation_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado
        Map<String, Object> reservationInfo = new HashMap<>();
        // ... (rellena reservationInfo) ...

        ResponseEntity<?> response = userController.newSportReservation(request, reservationInfo);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testHasReservation_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<Boolean> response = userController.hasReservation(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testDeletereservation_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<?> response = userController.deletereservation(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetUserReservation_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<SportReservation> response = userController.getUserReservation(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testNewAttendance_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<?> response = userController.newAttendance(request, "testCode");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetCarnet_whenUserIsAuthenticated() throws IOException, SQLException {
        // Simula la solicitud HTTP
        MockHttpServletRequest request = new MockHttpServletRequest();
        // Simula el Principal
        Principal principal = mock(Principal.class);
        request.setUserPrincipal(principal);
        when(principal.getName()).thenReturn("test@example.com");

        // Simula el usuario
        User user = new User(1L, "John", "Doe", "Smith", "12345678A", "mariscalalonso16@icloud.com",
                "123");
        user.setRoles(Arrays.asList("USER"));
        when(userService.findByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<?> response = userController.getCarnet(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody() != null);
    }

    @Test
    void testGetCarnet_whenUserIsNotAuthenticated() throws IOException, SQLException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity<?> response = userController.getCarnet(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGet_me_whenUserIsAuthenticated() throws IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        Principal principal = mock(Principal.class);
        request.setUserPrincipal(principal);
        when(principal.getName()).thenReturn("test@example.com");

        User user = new User(1L, "John", "Doe", "Smith", "12345678A", "mariscalalonso16@icloud.com",
                "123");
        when(userService.findByEmail("test@example.com")).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.get_me(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGet_me_whenUserIsNotAuthenticated() throws IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        // No principal is set in the request

        // Act
        ResponseEntity<User> response = userController.get_me(request);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}
