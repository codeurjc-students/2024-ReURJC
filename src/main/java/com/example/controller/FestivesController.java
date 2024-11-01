package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.controller.Responses.FestiveInfo;
import com.example.controller.Responses.SubjectScheduleResponse;
import com.example.model.Festive;
import com.example.services.FestiveService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class FestivesController {

    @Autowired
    private FestiveService service;


    @GetMapping("/api/festives")
    public ResponseEntity<?> getMethodName() {
        List<FestiveInfo> response = new ArrayList<FestiveInfo>();
        for (Festive festive : service.getAll()) {
            response.add(new FestiveInfo(festive.getDay(), festive.getMonth(), festive.getYear(), festive.getColor(), festive.getStartedXDaysAgo(), festive.getLocal()));
        }
        return ResponseEntity.ok(response);
    }
    
    
}
