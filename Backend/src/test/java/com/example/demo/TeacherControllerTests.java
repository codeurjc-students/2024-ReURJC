package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.controller.TeacherController;
import com.example.model.Attendance;
import com.example.model.Subject;
import com.example.model.User;
import com.example.services.AttendanceService;
import com.example.services.SubjectService;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
public class TeacherControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private SubjectService subjectService;

    @InjectMocks
    private TeacherController teacherController;

    @Test
    void testNewAttendance_whenUserIsAuthenticatedAndIsTeacher() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        user.setRoles(List.of("TEACHER")); // Usuario con rol de profesor
        Subject subject = new Subject();
        Attendance attendance = new Attendance();

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(subjectService.getSubject(1L)).thenReturn(subject);
        when(attendanceService.newAttendance(user, subject)).thenReturn(attendance);
        when(request.getRequestURI()).thenReturn("/api/v1/teacher/newAttendance");

        ResponseEntity<Attendance> response = teacherController.newAttendance(request, 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testNewAttendance_whenUserIsAuthenticatedAndIsNotTeacher() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        user.setRoles(List.of("STUDENT")); // Usuario con rol de estudiante
        Subject subject = new Subject();

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(subjectService.getSubject(1L)).thenReturn(subject);

        ResponseEntity<Attendance> response = teacherController.newAttendance(request, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testNewAttendance_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<Attendance> response = teacherController.newAttendance(request, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetAllaTTENDANCES_whenUserIsAuthenticatedAndIsTeacher() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        user.setRoles(List.of("TEACHER")); // Usuario con rol de profesor
        List<Attendance> attendances = Collections.singletonList(new Attendance());

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(attendanceService.getAllAttendances(user)).thenReturn(attendances);

        ResponseEntity<List<Attendance>> response = teacherController.getAllaTTENDANCES(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(attendances, response.getBody());
    }

    @Test
    void testGetAllaTTENDANCES_whenUserIsAuthenticatedAndIsNotTeacher() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        user.setRoles(List.of("STUDENT")); // Usuario con rol de estudiante

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<List<Attendance>> response = teacherController.getAllaTTENDANCES(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetAllaTTENDANCES_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null); // Usuario no autenticado

        ResponseEntity<List<Attendance>> response = teacherController.getAllaTTENDANCES(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}