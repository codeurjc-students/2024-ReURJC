package com.example.demo;

import com.example.controller.MoodleController;
import com.example.model.NotificationRequest;
import com.example.model.Subject;
import com.example.model.Subject_Mark;
import com.example.model.User;
import com.example.services.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MoodleControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private SubjectMarkService subjectMarkService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private FCMService fcmService;

    @InjectMocks
    private MoodleController moodleController;

    @Test
    void testMiEndpoint_createNewMark() throws Exception {
        Map<String, Object> datos = new HashMap<>();
        datos.put("userid", "1");
        datos.put("courseid", "10");
        datos.put("grade", "8");
        datos.put("assignmentname", "Examen 1");

        User student = new User();
        Subject subject = new Subject();

        when(userService.findById(1L)).thenReturn(student);
        when(subjectService.getSubject(10L)).thenReturn(subject);
        when(subjectMarkService.existsByStudentIdAndSubjectIdAndNameMark(any(), any(), anyString())).thenReturn(false);
        when(subjectMarkService.save(any(Subject_Mark.class))).thenReturn(1L);

        ResponseEntity<URI> response = moodleController.miEndpoint(datos);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());;
    }

    @Test
    void testMiEndpoint_updateExistingMark() throws Exception {
        Map<String, Object> datos = new HashMap<>();
        datos.put("userid", "1");
        datos.put("courseid", "10");
        datos.put("grade", "9");
        datos.put("assignmentname", "Examen 1");

        User student = new User();
        Subject subject = new Subject();
        Subject_Mark existingMark = new Subject_Mark();

        when(userService.findById(1L)).thenReturn(student);
        when(subjectService.getSubject(10L)).thenReturn(subject);
        when(subjectMarkService.existsByStudentIdAndSubjectIdAndNameMark(any(), any(), anyString())).thenReturn(true);
        when(subjectMarkService.findByStudentIdAndSubjectIdAndNameMark(any(), any(), anyString())).thenReturn(java.util.Optional.of(existingMark));
        when(subjectMarkService.save(any(Subject_Mark.class))).thenReturn(1L);

        ResponseEntity<URI> response = moodleController.miEndpoint(datos);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testMiEndpoint_invalidInput() throws Exception {
        Map<String, Object> datos = new HashMap<>();
        datos.put("userid", "abc"); // ID de usuario inválido

        ResponseEntity<URI> response = moodleController.miEndpoint(datos);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testMiEndpoint_userNotFound() throws Exception {
        Map<String, Object> datos = new HashMap<>();
        datos.put("userid", "1");
        datos.put("courseid", "10");
        datos.put("grade", "8");
        datos.put("assignmentname", "Examen 1");

        when(userService.findById(1L)).thenThrow(new RuntimeException()); // Simula que el usuario no se encuentra

        ResponseEntity<URI> response = moodleController.miEndpoint(datos);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}