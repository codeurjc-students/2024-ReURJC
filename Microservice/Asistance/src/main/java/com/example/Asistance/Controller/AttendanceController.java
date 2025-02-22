package com.example.Asistance.Controller;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Asistance.Model.Attendance;

import com.example.Asistance.Model.Subject;
import com.example.Asistance.Model.User;
import com.example.Asistance.services.AttendanceService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/new")
    public ResponseEntity<Attendance> newAttendance(
            HttpServletRequest request,
            @RequestBody Subject subject,
            @RequestParam(value = "id") Long creatorId) {
        Attendance attendance = attendanceService.newAttendance(creatorId, subject);
        URI location = URI.create(request.getRequestURI() + "/" + attendance.getCode());
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/newAttendance")
    public ResponseEntity<URI> newAttendance(
            HttpServletRequest request,
            @RequestParam String code,
            @RequestBody User user) {

        Attendance attendance = attendanceService.getAttendanceEvent(code);

        if (attendance != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime fiveMinutesAgo = now.minusMinutes(5).plusHours(1);
            if (attendance.getDateTime().isAfter(fiveMinutesAgo)) {
                attendanceService.adduser(attendance, user);
                URI location = URI.create(request.getRequestURI() + "/" + user.getStudentId());
                return ResponseEntity.created(location).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/attendances")
    public ResponseEntity<List<Attendance>> getAllAttendances(@RequestParam("id") Long userId) {
        return ResponseEntity.ok(attendanceService.getAllAttendances(userId));
    }

    @PutMapping("/attendances/{id}/add-time")
    public ResponseEntity<Boolean> addTimeToAttendance(@RequestParam("id") Long userId ,@PathVariable Long id, HttpServletRequest request) {

        try {
            Attendance attendance = attendanceService.getAttendanceById(id);

            if (!attendance.getCreator().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (attendanceService.addTime(id)) {
                return ResponseEntity.ok().body(true);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        

        
    }

}
