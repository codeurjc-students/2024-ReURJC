package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.controller.NewsController;
import com.example.model.News;
import com.example.services.NewService;

@SpringBootTest
public class NewsControllerTests {

    @Mock
    private NewService newService;

    @InjectMocks
    private NewsController newsController;

    @Test
    void testGetNewsPageableNewer() {
        int pageNumber = 0;
        List<News> newsList = Collections.singletonList(new News());

        when(newService.getAllNewer(pageNumber)).thenReturn(newsList);

        ResponseEntity<List<News>> response = newsController.getNewsPageableNewer(pageNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newsList, response.getBody());
    }
}