package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.controller.FestivesController;
import com.example.model.Festive;
import com.example.services.FestiveService;

@SpringBootTest
public class FestivesControllerTests {

    @Mock
    private FestiveService festiveService;

    @InjectMocks
    private FestivesController festivesController;

    @Test
    void testGetMethodName() {
        List<Festive> festives = Collections.singletonList(new Festive());

        when(festiveService.getAll()).thenReturn(festives);

        ResponseEntity<List<Festive>> response = festivesController.getMethodName();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(festives, response.getBody());
    }
}