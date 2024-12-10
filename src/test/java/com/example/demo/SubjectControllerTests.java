package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.controller.Responses.SubjectScheduleResponse;
import com.example.controller.SubjectsController;
import com.example.model.Schedule;
import com.example.model.Subject;
import com.example.model.User;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
public class SubjectControllerTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private SubjectsController subjectsController;

    @Test
    void testGetSchedule_whenUserIsAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        // Crea algunas asignaturas con horarios
        Collection<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1L, "Matemáticas"));
        subjects.add(new Subject(2L, "Física"));
        user.getSubjects().addAll(subjects);

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<List<SubjectScheduleResponse>> response = subjectsController.getSchedule(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verifica que la respuesta contiene la información de las asignaturas y sus horarios
        assertEquals(2, response.getBody().size()); 
    }

    @Test
    void testGetSchedule_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<List<SubjectScheduleResponse>> response = subjectsController.getSchedule(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetSubjects_whenUserIsAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        // Crea algunas asignaturas
        Collection<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1L, "Matemáticas"));
        subjects.add(new Subject(2L, "Física"));
        user.getSubjects().addAll(subjects);

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<List<Subject>> response = subjectsController.getSubjects(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verifica que la respuesta contiene la información de las asignaturas
        assertEquals(2, response.getBody().size()); 
    }

    @Test
    void testGetSubjects_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<List<Subject>> response = subjectsController.getSubjects(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}