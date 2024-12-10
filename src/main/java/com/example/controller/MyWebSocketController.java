package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Subject_Mark;
import com.example.services.SubjectMarkService;

@RestController
public class MyWebSocketController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private SubjectMarkService subjectMarkService;

    public ResponseEntity<Subject_Mark> sendUpdate() throws Exception {
        Subject_Mark lastSubjectMark = subjectMarkService.getLastSubjectMarkAdded();
        template.convertAndSend("/topic/newGrade", lastSubjectMark);
        return ResponseEntity.ok(lastSubjectMark);
    }
}