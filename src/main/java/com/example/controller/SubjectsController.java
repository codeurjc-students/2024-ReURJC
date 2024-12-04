package com.example.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

import com.example.controller.Responses.SubjectScheduleResponse;
import com.example.model.Subject;
import com.example.model.User;
import com.example.repository.SubjectRepository;
import com.example.services.UserService;
import com.example.services.securityServices.jwt.AuthResponse;
import com.example.services.securityServices.jwt.AuthResponse.Status;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/subjects")
public class SubjectsController {

    @Autowired
    private UserService userService;


    
    @GetMapping("/schedule")
    public ResponseEntity<?> getSchedule(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
        User user = userService.findByEmail(principal.getName());

        Collection<Subject> subject = user.getSubjects();
        List<SubjectScheduleResponse> response = new ArrayList<SubjectScheduleResponse>();
        for (Subject subjectElem : subject) {
            response.add(new SubjectScheduleResponse(subjectElem.getTitle(), subjectElem.getSchedule()));
        }
            return ResponseEntity.ok(response);

    } 

        return ResponseEntity.ok(new AuthResponse(Status.FAILURE, "You must login!", true));
    
        
    } 
    

    
    

    
}
