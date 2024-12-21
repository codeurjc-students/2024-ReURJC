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

import com.example.controller.SubjectMarkController;
import com.example.model.Subject_Mark;
import com.example.model.User;
import com.example.services.SubjectMarkService;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
public class SubjectMarkControllerTests {

    @Mock
    private SubjectMarkService subjectMarkService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SubjectMarkController subjectMarkController;

    @Test
    void testGetMethodName_whenUserIsAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User user = new User();
        List<Subject_Mark> marks = Collections.singletonList(new Subject_Mark()); // Lista de notas

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(subjectMarkService.findSubjectsByStudent(user)).thenReturn(marks);

        ResponseEntity<List<Subject_Mark>> response = subjectMarkController.getMethodName(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(marks, response.getBody());
    }

    @Test
    void testGetMethodName_whenUserIsNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null);

        ResponseEntity<List<Subject_Mark>> response = subjectMarkController.getMethodName(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}