package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.services.FestiveService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class FestivesController {

    @Autowired
    private FestiveService service;


    @GetMapping("/api/festives")
    public ResponseEntity<?> getMethodName() {
        return ResponseEntity.ok(service.getAll());
    }
    
    
}
