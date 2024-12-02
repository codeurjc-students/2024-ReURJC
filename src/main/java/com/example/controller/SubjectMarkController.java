package com.example.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Notification;
import com.example.model.Subject_Mark;
import com.example.model.User;
import com.example.services.SubjectMarkService;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/marks")
public class SubjectMarkController {

    @Autowired
    private SubjectMarkService subjectMarkService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<Subject_Mark>> getMethodName(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
    if (principal != null) {
        User user = userService.findByEmail(principal.getName());
        List<Subject_Mark> record = subjectMarkService.findSubjectsByStudent(user);
        return ResponseEntity.ok(record);

    }
    return ResponseEntity.notFound().build();
    }
    
    
}
