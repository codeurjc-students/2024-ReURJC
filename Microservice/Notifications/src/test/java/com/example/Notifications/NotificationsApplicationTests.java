package com.example.Notifications;

import com.example.Notifications.controller.NotificationController;
import com.example.Notifications.model.Notification;
import com.example.Notifications.model.NotificationRequest;
import com.example.Notifications.service.FCMService;
import com.example.Notifications.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NotificationsApplicationTests {

	@Mock
    private NotificationService notificationService; // Mocked notification service

    @Mock
    private FCMService fcmService; // Mocked FCM service

    @Mock
    private RestTemplate restTemplate; // Mocked HTTP client for the main microservice

    @InjectMocks
    private NotificationController notificationController; // Controller under test

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks before each test
    }

    // --- Tests for getAllNotifications ---

    /**
     * Test: Getting notifications for a user with notifications should return a list with HTTP 200 OK.
     */
    @Test
    void getAllNotifications_userWithNotifications_returnsNotifications() {
        // Arrange
        Long userId = 1L;
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification(userId, "New grade in Math", "Assignment1 has been graded"));
        notifications.add(new Notification(userId, "New grade in Physics", "Assignment2 has been graded"));
        when(notificationService.findAllByUser(userId)).thenReturn(notifications);

        // Act
        ResponseEntity<List<Notification>> response = notificationController.getAllNotifications(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notifications, response.getBody());
        verify(notificationService).findAllByUser(userId);
    }

    /**
     * Test: Getting notifications for a user without notifications should return an empty list with HTTP 200 OK.
     */
    @Test
    void getAllNotifications_userWithoutNotifications_returnsEmptyList() {
        // Arrange
        Long userId = 1L;
        List<Notification> notifications = new ArrayList<>();
        when(notificationService.findAllByUser(userId)).thenReturn(notifications);

        // Act
        ResponseEntity<List<Notification>> response = notificationController.getAllNotifications(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(notificationService).findAllByUser(userId);
    }

    /**
     * Test: If the service throws an exception when getting notifications, it should propagate as a RuntimeException.
     */
    @Test
    void getAllNotifications_serviceThrowsException_throwsRuntimeException() {
        // Arrange
        Long userId = 1L;
        when(notificationService.findAllByUser(userId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> notificationController.getAllNotifications(userId));
        verify(notificationService).findAllByUser(userId);
    }

    // --- Tests for updateGrade ---

    /**
     * Test: Updating a grade with valid data should update the grade, create a local notification, and send push notifications with HTTP 200 OK.
     */
    @Test
    void updateGrade_validData_updatesGradeAndSendsNotifications() throws Exception {
        // Arrange
        Map<String, Object> datos = Map.of(
            "userid", "1",
            "subjectTitle", "Math",
            "assignmentname", "Assignment1",
            "finalMark", "90"
        );
        List<String> fcmTokens = List.of("token1", "token2");
        Map<String, Object> updatedData = Map.of(
            "subjectTitle", "Math",
            "finalMark", "90",
            "fcmTokens", fcmTokens
        );
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(updatedData, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(responseEntity);

        // Act
        ResponseEntity<Void> response = notificationController.updateGrade(datos);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(notificationService).newNote(1L, "Math", "Assignment1", "90", "Ordinaria");
        verify(fcmService, times(2)).sendMessageToToken(any(NotificationRequest.class));
        verify(restTemplate).postForEntity(anyString(), any(), eq(Map.class));
    }

    /**
     * Test: Updating a grade with an invalid userId (-1) should return HTTP 400 Bad Request.
     */
    @Test
    void updateGrade_invalidUserId_returnsBadRequest() throws Exception {
        // Arrange
        Map<String, Object> datos = Map.of(
            "userid", "-1",
            "subjectTitle", "Math",
            "assignmentname", "Assignment1",
            "finalMark", "90"
        );

        // Act
        ResponseEntity<Void> response = notificationController.updateGrade(datos);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(Map.class));
        verify(notificationService, never()).newNote(anyLong(), anyString(), anyString(), anyString(), anyString());
        verify(fcmService, never()).sendMessageToToken(any(NotificationRequest.class));
    }

    /**
     * Test: Updating a grade with a non-numeric userId should return HTTP 400 Bad Request.
     */
    @Test
    void updateGrade_nonNumericUserId_returnsBadRequest() throws Exception {
        // Arrange
        Map<String, Object> datos = Map.of(
            "userid", "not_a_number",
            "subjectTitle", "Math",
            "assignmentname", "Assignment1",
            "finalMark", "90"
        );

        // Act
        ResponseEntity<Void> response = notificationController.updateGrade(datos);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(Map.class));
        verify(notificationService, never()).newNote(anyLong(), anyString(), anyString(), anyString(), anyString());
        verify(fcmService, never()).sendMessageToToken(any(NotificationRequest.class));
    }


    /**
     * Test: If there are no FCM tokens, it should create the local notification but not send push notifications, returning HTTP 200 OK.
     */
    @Test
    void updateGrade_noFcmTokens_createsLocalNotificationOnly() throws Exception {
        // Arrange
        Map<String, Object> datos = Map.of(
            "userid", "1",
            "subjectTitle", "Math",
            "assignmentname", "Assignment1",
            "finalMark", "90"
        );
        List<String> fcmTokens = new ArrayList<>();
        Map<String, Object> updatedData = Map.of(
            "subjectTitle", "Math",
            "finalMark", "90",
            "fcmTokens", fcmTokens
        );
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(updatedData, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(responseEntity);

        // Act
        ResponseEntity<Void> response = notificationController.updateGrade(datos);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(notificationService).newNote(1L, "Math", "Assignment1", "90", "Ordinaria");
        verify(fcmService, never()).sendMessageToToken(any(NotificationRequest.class));
        verify(restTemplate).postForEntity(anyString(), any(), eq(Map.class));
    }

    /**
     * Test: With multiple FCM tokens, it should send notifications to all and return HTTP 200 OK.
     */
    @Test
    void updateGrade_multipleFcmTokens_sendsNotificationsToAll() throws Exception {
        // Arrange
        Map<String, Object> datos = Map.of(
            "userid", "1",
            "subjectTitle", "Math",
            "assignmentname", "Assignment1",
            "finalMark", "90"
        );
        List<String> fcmTokens = List.of("token1", "token2", "token3");
        Map<String, Object> updatedData = Map.of(
            "subjectTitle", "Math",
            "finalMark", "90",
            "fcmTokens", fcmTokens
        );
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(updatedData, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(responseEntity);

        // Act
        ResponseEntity<Void> response = notificationController.updateGrade(datos);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(notificationService).newNote(1L, "Math", "Assignment1", "90", "Ordinaria");
        verify(fcmService, times(3)).sendMessageToToken(any(NotificationRequest.class));
        verify(restTemplate).postForEntity(anyString(), any(), eq(Map.class));
    }

    

}
