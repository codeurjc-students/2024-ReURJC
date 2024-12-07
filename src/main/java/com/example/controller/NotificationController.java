package com.example.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Notification;
import com.example.model.User;
import com.example.services.NotificationService;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<Notification>> getAllNotifications(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            List<Notification> notifications = notificationService.findAllByUser(user);
            return new ResponseEntity<>(notifications, HttpStatus.OK);

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }
}
