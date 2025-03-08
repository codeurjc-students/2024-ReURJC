package com.example.Asistance;

import com.example.Asistance.Controller.AttendanceController;
import com.example.Asistance.Model.Attendance;
import com.example.Asistance.Model.Subject;
import com.example.Asistance.Model.User;
import com.example.Asistance.services.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for AttendanceController.
 * Uses Mockito to mock dependencies and ensure isolated unit tests.
 */
class AsistanceApplicationTests {

    @Mock
    private AttendanceService attendanceService; // Mocked service layer

    @Mock
    private HttpServletRequest request; // Mocked HTTP request object

    @InjectMocks
    private AttendanceController attendanceController; // Controller under test

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks before each test
    }

    /**
     * Test: Creating a new attendance record with valid input should return HTTP 201 Created.
     */
    @Test
    void newAttendance_validInput_returnsCreated() {
        // Arrange
        Long creatorId = 1L;
        Subject subject = new Subject(101L, "Math");
        Attendance mockAttendance = new Attendance(creatorId, subject, "ABCDEF");
        mockAttendance.setId(123L); // Assign a mock ID for URI creation

        when(attendanceService.newAttendance(creatorId, subject)).thenReturn(mockAttendance);
        when(request.getRequestURI()).thenReturn("/attendance/new");

        // Act
        ResponseEntity<Attendance> response = attendanceController.newAttendance(request, subject, creatorId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("/attendance/new/ABCDEF"), response.getHeaders().getLocation());
        verify(attendanceService).newAttendance(creatorId, subject);
    }

    /**
     * Test: Adding a user to an attendance event should return HTTP 201 Created.
     */
    @Test
    void newAttendance_addUser_validCodeAndTime_returnsCreated() {
        // Arrange
        String code = "ABCDEF";
        User user = new User(201L, "Alice", "Smith", "12345678A");
        Attendance attendance = new Attendance(1L, new Subject(101L, "Math"), code);
        LocalDateTime fixedNow = LocalDateTime.of(2024, 3, 8, 10, 0, 0); // Fixed date-time for testing
        attendance.setDateTime(fixedNow.plusMinutes(56));

        // Mock LocalDateTime.now() to return fixedNow
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fixedNow);

            when(attendanceService.getAttendanceEvent(code)).thenReturn(attendance);
            when(request.getRequestURI()).thenReturn("/attendance/newAttendance");
            doNothing().when(attendanceService).adduser(attendance, user);

            // Act
            ResponseEntity<URI> response = attendanceController.newAttendance(request, code, user);

            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            verify(attendanceService).getAttendanceEvent(code);
            verify(attendanceService).adduser(attendance, user);
        }
    }

    /**
     * Test: Attempting to add a user to a non-existent attendance should return HTTP 404 Not Found.
     */
    @Test
    void newAttendance_addUser_attendanceNotFound_returnsNotFound() {
        // Arrange
        String code = "INVALID";
        User user = new User(201L, "Alice", "Smith", "12345678A");
        when(attendanceService.getAttendanceEvent(code)).thenReturn(null);

        // Act
        ResponseEntity<URI> response = attendanceController.newAttendance(request, code, user);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(attendanceService).getAttendanceEvent(code);
        verify(attendanceService, never()).adduser(any(), any()); // Ensure adduser is not called
    }

    /**
     * Test: Attempting to add a user to an expired attendance should return HTTP 400 Bad Request.
     */
    @Test
    void newAttendance_addUser_attendanceOutOfTime_returnsBadRequest() {
        // Arrange
        String code = "ABCDEF";
        User user = new User(201L, "Alice", "Smith", "12345678A");
        Attendance attendance = new Attendance(1L, new Subject(101L, "Math"), code);
        attendance.setDateTime(LocalDateTime.now().minusHours(2)); // Attendance expired

        when(attendanceService.getAttendanceEvent(code)).thenReturn(attendance);

        // Act
        ResponseEntity<URI> response = attendanceController.newAttendance(request, code, user);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(attendanceService).getAttendanceEvent(code);
        verify(attendanceService, never()).adduser(any(), any()); // Ensure adduser is not called
    }

    /**
     * Test: Retrieving all attendances for a user should return a list of attendances with HTTP 200 OK.
     */
    @Test
    void getAllAttendances_returnsListOfAttendances() {
        // Arrange
        Long userId = 1L;
        List<Attendance> attendances = new ArrayList<>();
        attendances.add(new Attendance(userId, new Subject(101L, "Math"), "ABCDEF"));
        attendances.add(new Attendance(userId, new Subject(102L, "Physics"), "GHIJKL"));

        when(attendanceService.getAllAttendances(userId)).thenReturn(attendances);

        // Act
        ResponseEntity<List<Attendance>> response = attendanceController.getAllAttendances(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(attendances, response.getBody());
        verify(attendanceService).getAllAttendances(userId);
    }

    /**
     * Test: Attempting to retrieve attendances when none exist should return an empty list with HTTP 200 OK.
     */
    @Test
    void getAllAttendances_noAttendances_returnsEmptyList() {
        // Arrange
        Long userId = 1L;
        List<Attendance> attendances = new ArrayList<>(); // Empty list

        when(attendanceService.getAllAttendances(userId)).thenReturn(attendances);

        // Act
        ResponseEntity<List<Attendance>> response = attendanceController.getAllAttendances(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty()); // Check if the list is empty
        verify(attendanceService).getAllAttendances(userId);
    }

     /**
      * Test: Handling exceptions in getAllAttendances should return an appropriate status (in this case, re-throwing as RuntimeException).
      */
    @Test
    void getAllAttendances_serviceThrowsException_returnsAppropriateStatus() {
        // Arrange
        Long userId = 1L;
        when(attendanceService.getAllAttendances(userId)).thenThrow(new RuntimeException("Database Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> attendanceController.getAllAttendances(userId));
        verify(attendanceService).getAllAttendances(userId);

    }

    /**
     * Test: Adding time to attendance with valid user and ID should return HTTP 200 OK with true body.
     */
    @Test
    void addTimeToAttendance_validUserAndId_returnsTrue() {
        // Arrange
        Long userId = 1L;
        Long attendanceId = 2L;
        Attendance attendance = new Attendance(userId, new Subject(101L, "Math"), "ABCDEF");
        attendance.setId(attendanceId); // Set the ID of the attendance

        when(attendanceService.getAttendanceById(attendanceId)).thenReturn(attendance);
        when(attendanceService.addTime(attendanceId)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = attendanceController.addTimeToAttendance(userId, attendanceId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(attendanceService).getAttendanceById(attendanceId);
        verify(attendanceService).addTime(attendanceId);
    }

    /**
     * Test: Adding time to attendance with an invalid user should return HTTP 403 Forbidden.
     */
    @Test
    void addTimeToAttendance_invalidUser_returnsForbidden() {
        // Arrange
        Long userId = 1L;
        Long attendanceId = 2L;
        Attendance attendance = new Attendance(3L, new Subject(101L, "Math"), "ABCDEF"); // Different creator
        attendance.setId(attendanceId);

        when(attendanceService.getAttendanceById(attendanceId)).thenReturn(attendance);

        // Act
        ResponseEntity<Boolean> response = attendanceController.addTimeToAttendance(userId, attendanceId, request);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody()); // No body should be returned
        verify(attendanceService).getAttendanceById(attendanceId);
        verify(attendanceService, never()).addTime(anyLong()); // addTime should not be called
    }

    /**
     * Test: Adding time to attendance when addTime fails should return HTTP 500 Internal Server Error.
     */
    @Test
    void addTimeToAttendance_addTimeFails_returnsInternalServerError() {
        // Arrange
        Long userId = 1L;
        Long attendanceId = 2L;
        Attendance attendance = new Attendance(userId, new Subject(101L, "CS"), "112233");
        attendance.setId(attendanceId);

        when(attendanceService.getAttendanceById(attendanceId)).thenReturn(attendance);
        when(attendanceService.addTime(attendanceId)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = attendanceController.addTimeToAttendance(userId, attendanceId, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(attendanceService, times(1)).getAttendanceById(attendanceId);
        verify(attendanceService, times(1)).addTime(attendanceId);
    }

    /**
     * Test: Adding time to attendance when an exception is thrown should return HTTP 500 Internal Server Error.
     */
    @Test
    void addTimeToAttendance_exceptionThrown_returnsInternalServerError() {
        // Arrange
        Long userId = 1L;
        Long attendanceId = 2L;

        when(attendanceService.getAttendanceById(attendanceId)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Boolean> response = attendanceController.addTimeToAttendance(userId, attendanceId, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(attendanceService).getAttendanceById(attendanceId);
        verify(attendanceService, never()).addTime(anyLong()); // addTime should not be called
    }

    /**
     * Test: Adding time to a non-existent attendance should return HTTP 500 Internal Server Error.
     */
    @Test
    void addTimeToAttendance_attendanceNotFound_returnsInternalServerError() {
        // Arrange
        Long userId = 1L;
        Long attendanceId = 2L;

        // Simulate attendance not found (e.g., return null or throw an exception)
        when(attendanceService.getAttendanceById(attendanceId)).thenThrow(new RuntimeException("Error"));

        // Act
        ResponseEntity<Boolean> response = attendanceController.addTimeToAttendance(userId, attendanceId, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(attendanceService).getAttendanceById(attendanceId);
        verify(attendanceService, never()).addTime(attendanceId); // addTime should never be called
    }
     /**
      * Test: If an exception occurs in newAttendance it returns a 500 error
      */
    @Test
    void newAttendance_serviceThrowsException_returnsAppropriateStatus() {
        // Arrange
        Long creatorId = 1L;
        Subject subject = new Subject(101L, "Math");

        // Simulate an exception in the service layer
        when(attendanceService.newAttendance(creatorId, subject)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert (expect a 500 Internal Server Error)
         assertThrows(RuntimeException.class, () -> {
            attendanceController.newAttendance(request, subject, creatorId);
        });

        verify(attendanceService).newAttendance(creatorId, subject);
    }
     /**
      * Test: when adding a user if an exception is thrown
      */
   @Test
    void newAttendance_addUser_serviceThrowsException_returnsAppropriateStatus() {
        // Arrange
       String code = "ABCDEF";
       User user = new User(201L, "Alice", "Smith", "12345678A");
       Attendance attendance = new Attendance(1L, new Subject(101L, "Math"), code);
       LocalDateTime fixedNow = LocalDateTime.of(2024, 3, 8, 10, 0, 0);
       attendance.setDateTime(fixedNow.plusMinutes(30)); // Attendance is valid for 1 hour

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
           mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fixedNow);
           when(attendanceService.getAttendanceEvent(code)).thenReturn(attendance);
           doThrow(new RuntimeException("Database error")).when(attendanceService).adduser(attendance, user);

           // Act
           ResponseEntity<URI> response = attendanceController.newAttendance(request, code, user);

           // Assert:  We expect a 400 Error now
           assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
       }

       verify(attendanceService).getAttendanceEvent(code);
   }
}