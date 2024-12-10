package com.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Attendance;
import com.example.model.User;
import com.example.services.AttendanceService;
import com.example.services.SubjectService;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private SubjectService subjectService;

    @PostMapping("/newAttendance")
    public ResponseEntity<Attendance> newAttendance(HttpServletRequest request, @RequestParam Long subjectId) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("TEACHER")) {
               Attendance attendance  = attendanceService.newAttendance(user, subjectService.getSubject(subjectId));

                URI location = URI.create(request.getRequestURI() + "/" + attendance.getCode());
                return ResponseEntity.created(location).build();
            } else { 
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    
            }
        } 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        
    }

    @GetMapping("/attendances")
    public ResponseEntity<List<Attendance>> getAllaTTENDANCES(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("TEACHER"))  {
                


                return ResponseEntity.ok(attendanceService.getAllAttendances(user));
            
        } else { 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        }
    }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        

        
    }


}

