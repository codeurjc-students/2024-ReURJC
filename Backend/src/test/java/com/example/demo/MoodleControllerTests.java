/*package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.controller.MoodleController;
import com.example.model.NotificationRequest;
import com.example.model.Subject;
import com.example.model.Subject_Mark;
import com.example.model.User;
import com.example.services.FCMService;
import com.example.services.NotificationService;
import com.example.services.SubjectMarkService;
import com.example.services.SubjectService;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

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

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MoodleController moodleController;

    private User student;
    private Subject subject;

    @BeforeEach
    void setUp() {
        student = new User(1L, "John", "Doe", "Smith", "12345678A", "test@example.com",
                "123");

        subject = new Subject(1L, "Historia");
        Subject subject2 = new Subject(23L, "Matemáticas");
        student.getSubjects().addAll(List.of(subject, subject2));
        student.addFcmToken("token_de_prueba");
    }

    @Test
    void testUpdateGrade_ValidData_NewMark() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", "1");
        data.put("courseid", "1");
        data.put("grade", "8");
        data.put("assignmentname", "Exam 1");

        when(userService.findById(1L)).thenReturn(student);
        when(subjectService.getSubject(1L)).thenReturn(subject);
        when(subjectMarkService.existsByStudentIdAndSubjectIdAndNameMark(student, subject, "Exam 1"))
                .thenReturn(false);
        when(subjectMarkService.save(any(Subject_Mark.class))).thenReturn(10L);
        Subject_Mark newSubjectMark = new Subject_Mark(student, subject, 8, "Ordinaria", "Exam 1");
        when(subjectMarkService.getLastSubjectMarkAdded(student)).thenReturn(newSubjectMark);

        ResponseEntity<URI> response = moodleController.updateGrade(data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("/api/v1/events/10"), response.getHeaders().getLocation());

        verify(subjectMarkService, times(1)).save(any(Subject_Mark.class));
        verify(notificationService, times(1)).newNote(eq(student), eq(subject.getTitle()), eq("Exam 1"), eq("8"),
                eq("Ordinaria"));
        verify(fcmService, times(1)).sendMessageToToken(any(NotificationRequest.class));
        verify(messagingTemplate, times(1)).convertAndSendToUser(eq("test@example.com"),
                eq("/topic/private-messages"), any(Map.class));
    }

    @Test
    void testUpdateGrade_ValidData_UpdateMark() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", "1");
        data.put("courseid", "1");
        data.put("grade", "9");
        data.put("assignmentname", "Exam 1");
        Subject_Mark existingMark = new Subject_Mark(student, subject, 7, "Ordinaria", "Exam 1");

        when(userService.findById(1L)).thenReturn(student);
        when(subjectService.getSubject(1L)).thenReturn(subject);
        when(subjectMarkService.existsByStudentIdAndSubjectIdAndNameMark(student, subject, "Exam 1"))
                .thenReturn(true);
        when(subjectMarkService.findByStudentIdAndSubjectIdAndNameMark(student, subject, "Exam 1"))
                .thenReturn(Optional.of(existingMark));
        when(subjectMarkService.save(any(Subject_Mark.class))).thenReturn(10L);
        Subject_Mark newSubjectMark = new Subject_Mark(student, subject, 8, "Ordinaria", "Exam 1");
        when(subjectMarkService.getLastSubjectMarkAdded(student)).thenReturn(newSubjectMark);

        ResponseEntity<URI> response = moodleController.updateGrade(data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("/api/v1/events/10"), response.getHeaders().getLocation());

        verify(subjectMarkService, times(1)).save(any(Subject_Mark.class));
        verify(notificationService, times(1)).newNote(eq(student), eq(subject.getTitle()), eq("Exam 1"), eq("9"),
                eq("Ordinaria"));
        verify(fcmService, times(1)).sendMessageToToken(any(NotificationRequest.class));
        verify(messagingTemplate, times(1)).convertAndSendToUser(eq("test@example.com"),
                eq("/topic/private-messages"), any(Map.class));

        assertEquals(9, existingMark.getMark());
    }

    @Test
    void testUpdateGrade_InvalidUserId() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", "invalid");

        ResponseEntity<URI> response = moodleController.updateGrade(data);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateGrade_UserIdNotFound() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", "-1");

        ResponseEntity<URI> response = moodleController.updateGrade(data);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}**/