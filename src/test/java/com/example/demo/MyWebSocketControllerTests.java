package com.example.demo;

import com.example.controller.MyWebSocketController;
import com.example.model.Subject_Mark;
import com.example.services.SubjectMarkService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MyWebSocketControllerTests {

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private SubjectMarkService subjectMarkService;

    @InjectMocks
    private MyWebSocketController webSocketController;

    @Test
    void testSendUpdate() throws Exception {
        Subject_Mark subjectMark = new Subject_Mark(); // Crea una instancia de Subject_Mark
        when(subjectMarkService.getLastSubjectMarkAdded()).thenReturn(subjectMark);

        ResponseEntity<Subject_Mark> response = webSocketController.sendUpdate();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subjectMark, response.getBody());

        verify(template).convertAndSend("/topic/newGrade", subjectMark);
    }
}