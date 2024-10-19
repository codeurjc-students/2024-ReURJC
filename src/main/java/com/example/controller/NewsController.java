package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.services.NewService;


@RestController
public class NewsController {

    @Autowired
	private NewService service;

    @GetMapping("/api/news")
    public ResponseEntity<?> getNewsPageableNewer(@RequestParam int pageNumber) {
        return ResponseEntity.ok(service.getAllNewer(pageNumber));
    } 
    
}
